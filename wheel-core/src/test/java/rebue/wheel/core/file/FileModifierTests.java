package rebue.wheel.core.file;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import rebue.wheel.core.file.FileModifier.AddPosition;

import java.io.IOException;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class FileModifierTests {

    @Test
    public void test01() throws IOException {
        final String       fileName     = FileUtils.getClassesPath() + "AuthzAccount.java"; // 文件路径
        final FileModifier fileModifier = new FileModifier(fileName);
        fileModifier.modifyLine(".*= *new *HashSet.*", "\t*= *new *HashSet.*\\(0\\)", "");
        fileModifier.addLine(" *public *class *\\w* *implements *java.io.Serializable *\\{\\t*", //
                "	private static final long	serialVersionUID	= 1L;", //
                "\\t*private *static *final *long\\t*serialVersionUID\\t*= *1L;", AddPosition.AFTER);
        fileModifier.process();
    }
}