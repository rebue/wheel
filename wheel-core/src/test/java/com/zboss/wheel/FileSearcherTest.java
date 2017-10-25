package com.zboss.wheel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import com.zboss.wheel.file.FileSearcher;
import com.zboss.wheel.file.FileUtils;

public class FileSearcherTest {

	@Test
	public void test01() throws FileNotFoundException, IOException {
		String dirName = FileUtils.getProjectPath(); // 文件路径
		FileSearcher.searchFiles(dirName, ".*\\.java", file -> {
			try {
				System.out.println("test01 " + file.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Test
	public void test02() throws FileNotFoundException, IOException {
		String dirName = FileUtils.getProjectPath(); // 文件路径
		List<File> files = FileSearcher.searchFiles(dirName, Pattern.compile(".*\\.java"));
		for (File file : files) {
			System.out.println("test02 " + file.getCanonicalPath());
		}
	}
}
