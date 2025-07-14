package rebue.wheel.core.spring;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import cn.idev.excel.util.ListUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义Excel读取监听器
 * 本监听器用于批量处理Excel数据，每读取一定数量的数据后进行批量保存
 * 如果保存过程中出现异常，会记录错误日志，并通过回调函数处理错误
 *
 * @param <T> 通过Excel读取的数据类型
 */
@Slf4j
public class ExcelReadListener<T> implements ReadListener<T> {

    /** 每次批量保存的大小 */
    private final int                      batchSize;
    /** 用于临时存储读取的数据的列表 */
    private final List<T>                  cachedDataList;
    /** 保存数据的回调函数 */
    private final Consumer<T>              saveData;
    /** 处理保存数据错误的回调函数 */
    private final BiConsumer<T, Exception> errorOnSave;

    /**
     * 构造函数
     *
     * @param batchSize   每次批量保存的大小
     * @param saveData    保存数据的回调函数
     * @param errorOnSave 处理保存数据错误的回调函数
     */
    public ExcelReadListener(int batchSize, Consumer<T> saveData, BiConsumer<T, Exception> errorOnSave) {
        this.batchSize      = batchSize;
        this.cachedDataList = ListUtils.newArrayListWithExpectedSize(batchSize);
        this.saveData       = saveData;
        this.errorOnSave    = errorOnSave;
    }

    /**
     * 保存临时存储的数据
     * 如果临时存储列表为空，则直接返回
     * 在保存数据时，如果遇到异常会记录错误日志，并调用错误处理回调函数
     * 保存完成后清空临时存储列表
     */
    private void saveData() {
        if (cachedDataList.isEmpty()) {
            return;
        }
        for (T item : cachedDataList) {
            try {
                saveData.accept(item);
            } catch (Exception e) {
                log.error("导入失败: {}", item.toString(), e);
                errorOnSave.accept(item, e);
            }
        }

        // 存储完成清理 list
        cachedDataList.clear();
    }

    /**
     * 当解析到一条数据时调用
     * 将数据添加到临时存储列表中，如果列表大小达到批量保存的阈值，则调用保存数据的方法
     *
     * @param vo              解析到的数据对象
     * @param analysisContext 上下文对象，包含解析过程中的状态
     */
    @Override
    public void invoke(T vo, AnalysisContext analysisContext) {
        cachedDataList.add(vo);
        if (cachedDataList.size() >= this.batchSize) {
            saveData();
        }
    }

    /**
     * 当所有数据解析完毕后调用
     * 在这里再次调用保存数据的方法，以确保所有数据都被保存
     *
     * @param analysisContext 上下文对象，包含解析过程中的状态
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
    }
}
