package rebue.wheel.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rebue.wheel.api.exception.RuntimeExceptionX;
import rebue.wheel.core.idworker.IdWorker3Helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @since 1.7
 */
public class RandomEx {
    private static final Logger _log = LoggerFactory.getLogger(RandomEx.class);

    private static final String       factor1 = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String       factor2 = "1234567890";
    private static final String       factor3 = "1234567890abcdefghijklmnopqrstuvwxyz";
    private static final String       factor4 = "abcdefghijklmnopqrstuvwxyz";
    private static       SecureRandom random;

    static {
        try {
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public static Random getRandom() {
        return random;
    }

    /**
     * 随机生成UUID（不含破折号）
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    /**
     * 生成resultSize位的随机数(只包含数字和大小写的字母)
     */
    public static String random1(final int resultSize) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < resultSize; i++) {
            stringBuilder.append(factor1.charAt(random.nextInt(factor1.length())));
        }
        return stringBuilder.toString();
    }

    /**
     * 生成resultSize位的随机数(只包含数字)
     */
    public static String random2(final int resultSize) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < resultSize; i++) {
            stringBuilder.append(factor2.charAt(random.nextInt(factor2.length())));
        }
        return stringBuilder.toString();
    }

    /**
     * 生成resultSize位的随机数(只包含数字和小写字母)
     */
    public static String random3(final int resultSize) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < resultSize; i++) {
            stringBuilder.append(factor3.charAt(random.nextInt(factor3.length())));
        }
        return stringBuilder.toString();
    }

    /**
     * 生成resultSize位的随机数(只包含小写字母)
     */
    public static String random4(final int resultSize) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < resultSize; i++) {
            stringBuilder.append(factor4.charAt(random.nextInt(factor4.length())));
        }
        return stringBuilder.toString();
    }

    /**
     * 常见汉字字符集
     */
    public static final String COMMON_CN_CHARS = "\u7684\u4e00\u4e86\u662f\u6211\u4e0d\u5728\u4eba\u4eec\u6709\u6765\u4ed6\u8fd9\u4e0a\u7740\u4e2a\u5730\u5230\u5927\u91cc\u8bf4\u5c31\u53bb\u5b50\u5f97"
            + "\u4e5f\u548c\u90a3\u8981\u4e0b\u770b\u5929\u65f6\u8fc7\u51fa\u5c0f\u4e48\u8d77\u4f60\u90fd\u628a\u597d\u8fd8\u591a\u6ca1\u4e3a\u53c8\u53ef\u5bb6\u5b66"
            + "\u53ea\u4ee5\u4e3b\u4f1a\u6837\u5e74\u60f3\u751f\u540c\u8001\u4e2d\u5341\u4ece\u81ea\u9762\u524d\u5934\u9053\u5b83\u540e\u7136\u8d70\u5f88\u50cf\u89c1"
            + "\u4e24\u7528\u5979\u56fd\u52a8\u8fdb\u6210\u56de\u4ec0\u8fb9\u4f5c\u5bf9\u5f00\u800c\u5df1\u4e9b\u73b0\u5c71\u6c11\u5019\u7ecf\u53d1\u5de5\u5411\u4e8b"
            + "\u547d\u7ed9\u957f\u6c34\u51e0\u4e49\u4e09\u58f0\u4e8e\u9ad8\u624b\u77e5\u7406\u773c\u5fd7\u70b9\u5fc3\u6218\u4e8c\u95ee\u4f46\u8eab\u65b9\u5b9e"
            + "\u5403\u505a\u53eb\u5f53\u4f4f\u542c\u9769\u6253\u5462\u771f\u5168\u624d\u56db\u5df2\u6240\u654c\u4e4b\u6700\u5149\u4ea7\u60c5\u8def\u5206\u603b\u6761"
            + "\u767d\u8bdd\u4e1c\u5e2d\u6b21\u4eb2\u5982\u88ab\u82b1\u53e3\u653e\u513f\u5e38\u6c14\u4e94\u7b2c\u4f7f\u5199\u519b\u5427\u6587\u8fd0\u518d\u679c"
            + "\u600e\u5b9a\u8bb8\u5feb\u660e\u884c\u56e0\u522b\u98de\u5916\u6811\u7269\u6d3b\u90e8\u95e8\u65e0\u5f80\u8239\u671b\u65b0\u5e26\u961f\u5148\u529b"
            + "\u5b8c\u5374\u7ad9\u4ee3\u5458\u673a\u66f4\u4e5d\u60a8\u6bcf\u98ce\u7ea7\u8ddf\u7b11\u554a\u5b69\u4e07\u5c11\u76f4\u610f\u591c\u6bd4\u9636"
            + "\u8fde\u8f66\u91cd\u4fbf\u6597\u9a6c\u54ea\u5316\u592a\u6307\u53d8\u793e\u4f3c\u58eb\u8005\u5e72\u77f3\u6ee1\u65e5\u51b3\u767e\u539f\u62ff\u7fa4"
            + "\u7a76\u5404\u516d\u672c\u601d\u89e3\u7acb\u6cb3\u6751\u516b\u96be\u65e9\u8bba\u5417\u6839\u5171\u8ba9\u76f8\u7814\u4eca\u5176\u4e66\u5750"
            + "\u63a5\u5e94\u5173\u4fe1\u89c9\u6b65\u53cd\u5904\u8bb0\u5c06\u5343\u627e\u4e89\u9886\u6216\u5e08\u7ed3\u5757\u8dd1\u8c01\u8349\u8d8a\u5b57\u52a0\u811a\u7d27\u7231\u7b49\u4e60\u9635\u6015\u6708\u9752\u534a\u706b\u6cd5\u9898\u5efa\u8d76\u4f4d\u5531\u6d77\u4e03\u5973\u4efb\u4ef6\u611f"
            + "\u51c6\u5f20\u56e2\u5c4b\u79bb\u8272\u8138\u7247\u79d1\u5012\u775b\u5229\u4e16\u521a\u4e14\u7531\u9001\u5207\u661f\u5bfc\u665a\u8868\u591f\u6574\u8ba4\u54cd\u96ea\u6d41\u672a\u573a\u8be5\u5e76\u5e95\u6df1\u523b\u5e73\u4f1f\u5fd9\u63d0\u786e\u8fd1\u4eae\u8f7b\u8bb2\u519c\u53e4\u9ed1"
            + "\u544a\u754c\u62c9\u540d\u5440\u571f\u6e05\u9633\u7167\u529e\u53f2\u6539\u5386\u8f6c\u753b\u9020\u5634\u6b64\u6cbb\u5317\u5fc5\u670d"
            + "\u96e8\u7a7f\u5185\u8bc6\u9a8c\u4f20\u4e1a\u83dc\u722c\u7761\u5174\u5f62\u91cf\u54b1\u89c2\u82e6\u4f53\u4f17\u901a\u51b2\u5408\u7834"
            + "\u53cb\u5ea6\u672f\u996d\u516c\u65c1\u623f\u6781\u5357\u67aa\u8bfb\u6c99\u5c81\u7ebf\u91ce\u575a\u7a7a\u6536\u7b97\u81f3\u653f\u57ce"
            + "\u52b3\u843d\u94b1\u7279\u56f4\u5f1f\u80dc\u6559\u70ed\u5c55\u5305\u6b4c\u7c7b\u6e10\u5f3a\u6570\u4e61\u547c\u6027\u97f3\u7b54\u54e5"
            + "\u9645\u65e7\u795e\u5ea7\u7ae0\u5e2e\u5566\u53d7\u7cfb\u4ee4\u8df3\u975e\u4f55\u725b\u53d6\u5165\u5cb8\u6562\u6389\u5ffd\u79cd\u88c5"
            + "\u9876\u6025\u6797\u505c\u606f\u53e5\u533a\u8863\u822c\u62a5\u53f6\u538b\u6162\u53d4\u80cc\u7ec6";

    /**
     * 随机生成汉字字符
     */
    public static char randomCnChar() {
        // return (char) (random.nextInt(300) + 19968);
        return COMMON_CN_CHARS.charAt(random.nextInt(COMMON_CN_CHARS.length()));
    }

    /**
     * 生成resultSize位的随机汉字
     */
    public static String randomCnStr(final int resultSize) {
        _log.info("生成{}位的随机汉字", resultSize);
        final char[] chars = new char[resultSize];
        for (int i = 0; i < resultSize; i++) {
            chars[i] = randomCnChar();
        }
        final String str = new String(chars);
        _log.debug("生成的随机汉字字符串是: {}", str);
        return str;
    }

    /**
     * 生成随机Boolean
     */
    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    /**
     * 生成随机日期
     */
    public static Date randomDate() {
        _log.info("生成随机日期");
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            final Date start = sdf.parse("1970-01-01");
            // 构造开始日期
            final Date end = new Date();                     // 构造结束日期
            // getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            final Date date = new Date(randomDate(start.getTime(), end.getTime()));
            _log.debug("生成的随机日期是: {}", sdf.format(date));
            return date;
        } catch (final ParseException e) {
            e.printStackTrace();
            throw new RuntimeExceptionX("不会发生的异常");
        }
    }

    private static long randomDate(final long begin, final long end) {
        final long rtn = begin + (long) (Math.random() * (end - begin));
        // 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
        if (rtn == begin || rtn == end) {
            return randomDate(begin, end);
        }
        return rtn;
    }

    /**
     * 随机生成手机号码
     */
    private static String[] telFirst = "130,131,132,133,134,135,136,137,138,139,150,151,152,153,155,156,157,158,159".split(",");

    /**
     * 随机生成手机号码
     */
    public static String randomMobile() {
        final int    index = random.nextInt(telFirst.length);
        final String first = telFirst[index];
        return first + random2(8);
    }

    /**
     * 随机生成Email
     */
    private static final String[] email_suffix = "@gmail.com,@yahoo.com,@msn.com,@hotmail.com,@aol.com,@ask.com,@live.com,@qq.com,@0355.net,@163.com,@163.net,@263.net,@3721.net,@yeah.net,@googlemail.com,@126.com,@sina.com,@sohu.com,@yahoo.com.cn"
            .split(",");

    /**
     * 随机生成Email
     */
    public static String randomEmail() {
        return random4(5) + email_suffix[random.nextInt(email_suffix.length)];
    }

    // 随机生成省、自治区、直辖市代码 1-2
    private static String[] provinces = {"11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43", "44", "45", "46", "50", "51",
            "52", "53", "54", "61", "62", "63", "64", "65", "71", "81", "82"
    };

    /**
     * 随机生成两位数的字符串（01-max）,不足两位的前面补0
     */
    private static String randomCityCode(final int max) {
        final int i = new Random().nextInt(max) + 1;
        return i > 9 ? i + "" : "0" + i;
    }

    /**
     * 随机生成生日
     */
    private static String randomBirthdayOfIdCard() {
        final SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
        return dft.format(randomDate());
    }

    public static String randomIdCard() {
        final String str = provinces[random.nextInt(provinces.length)] + randomCityCode(18) + randomCityCode(28) +//
                randomBirthdayOfIdCard() + (random.nextInt(899) + 100);
        return str + IdCardValidator.calcCheckBit(str);
    }

    /**
     * 随机生成地址
     */
    private static String[] area   = "广西南宁市,广东广州市,广东东莞市,陕西西安市,内蒙古包头市,四川成都市,福建厦门市,山东青岛市,山东大连市,浙江杭州市,浙江温州市,湖南长沙市,湖北武汉市,湖北襄阳市,江苏南昌市,河南洛阳市,河南开封市,北京市,深圳市,上海市,天津市,重庆市".split(",");
    private static String[] region = "朝阳区,城北区,江南区,永新区,新阳区,安庆区,青秀区,万秀区,临江区,高新区,万象区,闸北区".split(",");
    private static String[] road   = "重庆大厦,黑龙江路,十梅庵街,遵义路,湘潭街,瑞金广场,仙山街,仙山东路,仙山西大厦,白沙河路,赵红广场,机场路,民航街,长城南路,流亭立交桥,虹桥广场,长城大厦,礼阳路,风岗街,中川路,白塔广场,兴阳路,文阳街,绣城路,河城大厦,锦城广场,崇阳街,华城路,康城街,正阳路,和阳广场,中城路,江城大厦,顺城路,安城街,山城广场,春城街,国城路,泰城街,德阳路,明阳大厦,春阳路,艳阳街,秋阳路,硕阳街,青威高速,瑞阳街,丰海路,双元大厦,惜福镇街道,夏庄街道,古庙工业园,中山街,太平路,广西街,潍县广场,博山大厦,湖南路,济宁街,芝罘路,易州广场,荷泽四路,荷泽二街,荷泽一路,荷泽三大厦,观海二广场,广西支街,观海一路,济宁支街,莒县路,平度广场,明水路,蒙阴大厦,青岛路,湖北街,江宁广场,郯城街,天津路,保定街,安徽路,河北大厦,黄岛路,北京街,莘县路,济南街,宁阳广场,日照街,德县路,新泰大厦,荷泽路,山西广场,沂水路,肥城街,兰山路,四方街,平原广场,泗水大厦,浙江路,曲阜街,寿康路,河南广场,泰安路,大沽街,红山峡支路,西陵峡一大厦,台西纬一广场,台西纬四街,台西纬二路,西陵峡二街,西陵峡三路,台西纬三广场,台西纬五路,明月峡大厦,青铜峡路,台西二街,观音峡广场,瞿塘峡街,团岛二路,团岛一街,台西三路,台西一大厦,郓城南路,团岛三街,刘家峡路,西藏二街,西藏一广场,台西四街,三门峡路,城武支大厦,红山峡路,郓城北广场,龙羊峡路,西陵峡街,台西五路,团岛四街,石村广场,巫峡大厦,四川路,寿张街,嘉祥路,南村广场,范县路,西康街,云南路,巨野大厦,西江广场,鱼台街,单县路,定陶街,滕县路,钜野广场,观城路,汶上大厦,朝城路,滋阳街,邹县广场,濮县街,磁山路,汶水街,西藏路,城武大厦,团岛路,南阳街,广州路,东平街,枣庄广场,贵州街,费县路,南海大厦,登州路,文登广场,信号山支路,延安一街,信号山路,兴安支街,福山支广场,红岛支大厦,莱芜二路,吴县一街,金口三路,金口一广场,伏龙山路,鱼山支街,观象二路,吴县二大厦,莱芜一广场,金口二街,海阳路,龙口街,恒山路,鱼山广场,掖县路,福山大厦,红岛路,常州街,大学广场,龙华街,齐河路,莱阳街,黄县路,张店大厦,祚山路,苏州街,华山路,伏龙街,江苏广场,龙江街,王村路,琴屿大厦,齐东路,京山广场,龙山路,牟平街,延安三路,延吉街,南京广场,东海东大厦,银川西路,海口街,山东路,绍兴广场,芝泉路,东海中街,宁夏路,香港西大厦,隆德广场,扬州街,郧阳路,太平角一街,宁国二支路,太平角二广场,天台东一路,太平角三大厦,漳州路一路,漳州街二街,宁国一支广场,太平角六街,太平角四路,天台东二街,太平角五路,宁国三大厦,澳门三路,江西支街,澳门二路,宁国四街,大尧一广场,咸阳支街,洪泽湖路,吴兴二大厦,澄海三路,天台一广场,新湛二路,三明北街,新湛支路,湛山五街,泰州三广场,湛山四大厦,闽江三路,澳门四街,南海支路,吴兴三广场,三明南路,湛山二街,二轻新村镇,江南大厦,吴兴一广场,珠海二街,嘉峪关路,高邮湖街,湛山三路,澳门六广场,泰州二路,东海一大厦,天台二路,微山湖街,洞庭湖广场,珠海支街,福州南路,澄海二街,泰州四路,香港中大厦,澳门五路,新湛三街,澳门一路,正阳关街,宁武关广场,闽江四街,新湛一路,宁国一大厦,王家麦岛,澳门七广场,泰州一路,泰州六街,大尧二路,青大一街,闽江二广场,闽江一大厦,屏东支路,湛山一街,东海西路,徐家麦岛函谷关广场,大尧三路,晓望支街,秀湛二路,逍遥三大厦,澳门九广场,泰州五街,澄海一路,澳门八街,福州北路,珠海一广场,宁国二路,临淮关大厦,燕儿岛路,紫荆关街,武胜关广场,逍遥一街,秀湛四路,居庸关街,山海关路,鄱阳湖大厦,新湛路,漳州街,仙游路,花莲街,乐清广场,巢湖街,台南路,吴兴大厦,新田路,福清广场,澄海路,莆田街,海游路,镇江街,石岛广场,宜兴大厦,三明路,仰口街,沛县路,漳浦广场,大麦岛,台湾街,天台路,金湖大厦,高雄广场,海江街,岳阳路,善化街,荣成路,澳门广场,武昌路,闽江大厦,台北路,龙岩街,咸阳广场,宁德街,龙泉路,丽水街,海川路,彰化大厦,金田路,泰州街,太湖路,江西街,泰兴广场,青大街,金门路,南通大厦,旌德路,汇泉广场,宁国路,泉州街,如东路,奉化街,鹊山广场,莲岛大厦,华严路,嘉义街,古田路,南平广场,秀湛路,长汀街,湛山路,徐州大厦,丰县广场,汕头街,新竹路,黄海街,安庆路,基隆广场,韶关路,云霄大厦,新安路,仙居街,屏东广场,晓望街,海门路,珠海街,上杭路,永嘉大厦,漳平路,盐城街,新浦路,新昌街,高田广场,市场三街,金乡东路,市场二大厦,上海支路,李村支广场,惠民南路,市场纬街,长安南路,陵县支街,冠县支广场,小港一大厦,市场一路,小港二街,清平路,广东广场,新疆路,博平街,港通路,小港沿,福建广场,高唐街,茌平路,港青街,高密路,阳谷广场,平阴路,夏津大厦,邱县路,渤海街,恩县广场,旅顺街,堂邑路,李村街,即墨路,港华大厦,港环路,馆陶街,普集路,朝阳街,甘肃广场,港夏街,港联路,陵县大厦,上海路,宝山广场,武定路,长清街,长安路,惠民街,武城广场,聊城大厦,海泊路,沧口街,宁波路,胶州广场,莱州路,招远街,冠县路,六码头,金乡广场,禹城街,临清路,东阿街,吴淞路,大港沿,辽宁路,棣纬二大厦,大港纬一路,贮水山支街,无棣纬一广场,大港纬三街,大港纬五路,大港纬四街,大港纬二路,无棣二大厦,吉林支路,大港四街,普集支路,无棣三街,黄台支广场,大港三街,无棣一路,贮水山大厦,泰山支路,大港一广场,无棣四路,大连支街,大港二路,锦州支街,德平广场,高苑大厦,长山路,乐陵街,临邑路,嫩江广场,合江路,大连街,博兴路,蒲台大厦,黄台广场,城阳街,临淄路,安邱街,临朐路,青城广场,商河路,热河大厦,济阳路,承德街,淄川广场,辽北街,阳信路,益都街,松江路,流亭大厦,吉林路,恒台街,包头路,无棣街,铁山广场,锦州街,桓台路,兴安大厦,邹平路,胶东广场,章丘路,丹东街,华阳路,青海街,泰山广场,周村大厦,四平路,台东西七街,台东东二路,台东东七广场,台东西二路,东五街,云门二路,芙蓉山村,延安二广场,云门一街,台东四路,台东一街,台东二路,杭州支广场,内蒙古路,台东七大厦,台东六路,广饶支街,台东八广场,台东三街,四平支路,郭口东街,青海支路,沈阳支大厦,菜市二路,菜市一街,北仲三路,瑞云街,滨县广场,庆祥街,万寿路,大成大厦,芙蓉路,历城广场,大名路,昌平街,平定路,长兴街,浦口广场,诸城大厦,和兴路,德盛街,宁海路,威海广场,东山路,清和街,姜沟路,雒口大厦,松山广场,长春街,昆明路,顺兴街,利津路,阳明广场,人和路,郭口大厦,营口路,昌邑街,孟庄广场,丰盛街,埕口路,丹阳街,汉口路,洮南大厦,桑梓路,沾化街,山口路,沈阳街,南口广场,振兴街,通化路,福寺大厦,峄县路,寿光广场,曹县路,昌乐街,道口路,南九水街,台湛广场,东光大厦,驼峰路,太平山,标山路,云溪广场,太清路"
            .split(",");

    /**
     * 随机生成地址
     */
    public static String randomAddress() {
        final String first  = road[random.nextInt(road.length)];
        final String second = String.valueOf(random.nextInt(139) + 11) + "号";
        final String third  = random.nextInt(20) + 1 + "栋" + (random.nextInt(3) + 1) + "单元" + (random.nextInt(9) + 1) + "0" + (random.nextInt(6) + 1) + "室";
        return area[random.nextInt(area.length)] + region[random.nextInt(region.length)] + first + second + third;
    }

    /**
     * 随机生成人名
     */
    private static String firstName = "赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳鲍史唐费廉岑薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅皮卞齐康伍余元卜顾孟平黄和穆萧尹姚邵湛汪祁毛禹狄米贝明臧计伏成戴谈宋茅庞熊纪舒屈项祝董梁杜阮蓝闵席季麻强贾路娄危江童颜郭梅盛林刁钟徐邱骆高夏蔡田樊胡凌霍虞万支柯管卢莫经房裘缪干解应宗宣丁贲邓郁单杭洪包诸左石崔吉钮龚程嵇邢滑裴陆荣翁荀羊於惠甄魏加封芮羿储靳邴糜松井段富巫乌焦巴弓牧隗山谷车侯全郗班秋仲伊宫宁仇栾甘钭厉戎祖武符刘姜詹束龙叶司韶郜黎蓟薄印宿白怀蒲台从鄂索咸籍赖卓蔺屠蒙池乔阴郁胥能苍双闻莘党翟谭贡劳逄姬申扶堵冉宰郦雍却璩桑桂濮牛寿通边扈燕冀郏浦尚农温别庄晏柴瞿阎充慕连茹习宦艾鱼容向古易戈廖庚居衡步都耿满弘匡国文寇广禄阙东殴殳沃利蔚越夔隆师巩厍聂晁勾敖融冷辛阚那简饶空曾毋沙乜养鞠须丰巢关蒯相查后江红游竺权盖益桓公万俟司马上官欧阳夏侯诸葛闻人东方赫连皇甫尉迟公羊澹台公冶宗政濮阳淳于仲孙太叔申屠公孙乐正轩辕令狐钟离闾丘于官司寇仉子车孙端木巫马公西乐正公良夹谷宰父谷粱晋楚阎法汝涂钦段郭南门归海羊岳帅亢况后琴梁丘左商牟佘伯南宫墨谯年阳佟五言福百";
    private static String girl      = "秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽";
    private static String boy       = "伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘";

    public static String randomChineseName() {
        int          index  = random.nextInt(firstName.length());
        final String first  = firstName.substring(index, index + 1);
        final int    sex    = random.nextInt(2);
        String       str    = boy;
        int          length = boy.length();
        if (sex == 0) {
            str = girl;
            length = girl.length();
        } else {
        }
        index = random.nextInt(length);
        final String second   = str.substring(index, index + 1);
        final int    hasThird = random.nextInt(2);
        String       third    = "";
        if (hasThird == 1) {
            index = random.nextInt(length);
            third = str.substring(index, index + 1);
        }
        return first + second + third;
    }

    /**
     * 生成一个属性值随机的对象
     */
    public static Object randomPojo(final Class<?> clazz) {
        _log.info("创建一个属性值随机的对象");
        try {
            final Object model = clazz.newInstance();

            // 获取实体类的所有属性，返回Field数组
            final List<Field> fields = new ArrayList<>();
            @SuppressWarnings("rawtypes")
            Class tempClass = clazz;
            while (tempClass != null) {// 当父类为null的时候说明到达了最上层的父类(Object类).
                fields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
                tempClass = tempClass.getSuperclass(); // 得到父类,然后赋给自己
            }
            // 获取属性的名字
            for (final Field element : fields) {
                // 获取属性的名字
                String name = element.getName();
                // 获取属性类型
                final String type = element.getGenericType().toString();
                // 关键。。。可访问私有变量
                element.setAccessible(true);
                // 将属性的首字母大写
                name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                if (type.equals("class java.lang.String")) {
                    if (name.endsWith("Id")) {
                        final Method m = clazz.getMethod("set" + name, String.class);
                        m.invoke(model, random1(10));
                        continue;
                    }
                    if (name.endsWith("Code")) {
                        final Method m = clazz.getMethod("set" + name, String.class);
                        m.invoke(model, random1(10));
                        continue;
                    }
                    if (name.contains("Phone") || name.contains("Mobile") || name.contains("Tel")) {
                        final Method m = clazz.getMethod("set" + name, String.class);
                        m.invoke(model, randomMobile());
                        continue;
                    }
                    if (name.equals("IdCard") || name.equals("Idcard")) {
                        final Method m = clazz.getMethod("set" + name, String.class);
                        m.invoke(model, random2(18));
                        continue;
                    }
                    if (name.equals("Salt")) {
                        final Method m = clazz.getMethod("set" + name, String.class);
                        m.invoke(model, random1(6));
                        continue;
                    }
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, String.class);
                    m.invoke(model, randomCnStr(20));
                    continue;
                }
                if (type.equals("class java.lang.Long")) {
                    if (name.endsWith("Id")) {
                        final Method m = clazz.getMethod("set" + name, Long.class);
                        m.invoke(model, IdWorker3Helper.getId());
                        continue;
                    }
                    if (name.endsWith("Timestamp")) {
                        final Method m = clazz.getMethod("set" + name, Long.class);
                        m.invoke(model, System.currentTimeMillis());
                        continue;
                    }

                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Long.class);
                    m.invoke(model, Math.abs(random.nextLong()));
                    continue;
                }
                if (type.equals("class java.lang.Integer")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Integer.class);
                    m.invoke(model, Math.abs(random.nextInt()));
                    continue;
                }
                if (type.equals("class java.lang.Short")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Short.class);
                    m.invoke(model, (short) 1);
                    continue;
                }
                if (type.equals("class java.lang.Byte")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Byte.class);
                    m.invoke(model, (byte) (random.nextInt(2) + 1));
                    continue;
                }
                if (type.equals("class java.lang.Double")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Double.class);
                    m.invoke(model, random.nextDouble());
                    continue;
                }
                if (type.equals("class java.math.BigDecimal")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, BigDecimal.class);
                    m.invoke(model, BigDecimal.valueOf(random.nextDouble()));
                    continue;
                }
                if (type.equals("class java.lang.Boolean")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Boolean.class);
                    m.invoke(model, random.nextBoolean());
                    continue;
                }
                if (type.equals("class java.util.Date")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Date.class);
                    m.invoke(model, new Date());
                    continue;
                }
                if (type.equals("class java.time.LocalDateTime")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, LocalDateTime.class);
                    m.invoke(model, LocalDateTime.now());
                    continue;
                }
                if (type.equals("class java.time.LocalDate")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, LocalDate.class);
                    m.invoke(model, LocalDate.now());
                    continue;
                }
            }
            return model;
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeExceptionX("", e);
        }
    }
}