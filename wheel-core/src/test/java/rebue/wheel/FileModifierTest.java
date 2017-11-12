package rebue.wheel;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import rebue.wheel.file.FileModifier;
import rebue.wheel.file.FileModifier.AddPosition;
import rebue.wheel.file.FileUtils;

public class FileModifierTest {

	@Test
	public void test01() throws FileNotFoundException, IOException {
		String fileName = FileUtils.getClassesPath() + "AuthcAccount.java"; // 文件路径
		FileModifier fileModifier = new FileModifier(fileName);
		fileModifier.modifyLine(".*= *new *HashSet.*", "\t*= *new *HashSet.*\\(0\\)", "");
		fileModifier.addLine(" *public *class *\\w* *implements *java.io.Serializable *\\{\\t*", //
				"	private static final long	serialVersionUID	= 1L;", //
				"\\t*private *static *final *long\\t*serialVersionUID\\t*= *1L;", AddPosition.AFTER);
		fileModifier.process();
	}
}
