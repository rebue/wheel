package com.zboss.wheel.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件修改器
 * 
 * @since JDK1.8
 */
public final class FileModifier {

	private boolean			mIsModified	= false;

	private String			mFileName;

	private List<Adder>		mAdders		= new LinkedList<>();
	private List<Modifier>	mModifiers	= new LinkedList<>();
	private List<String>	mDeleters	= new LinkedList<>();

	public FileModifier(String fileName) {
		mFileName = fileName;
	}

	/**
	 * 添加一行<br>
	 * 如果exister不存在则将matcher匹配的行的前面或后面添加adder一行
	 * 
	 * @param matcher
	 * @param adder
	 * @param exister
	 */
	public void addLine(String matcher, String adder, String exister, AddPosition addPosition) {
		mAdders.add(new Adder(matcher, adder, exister, addPosition));
	}

	/**
	 * 修改一行
	 * 
	 * @param matcher
	 * @param older
	 * @param newer
	 */
	public void modifyLine(String matcher, String older, String newer) {
		mModifiers.add(new Modifier(matcher, older, newer));
	}

	public void delete(String deleter) {
		mDeleters.add(deleter);
	}

	public void process() throws IOException {
		List<String> lines;
		// 从文件中读取内容并在修改后放入列表
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(mFileName))) {
			lines = reader.lines().peek(line -> {
				// 线程安全的移除已存在项，不用添加
				Iterator<Adder> iterator = mAdders.iterator();
				while (iterator.hasNext()) {
					Adder adder = iterator.next();
					if (line.matches(adder.getExister()))
						iterator.remove();
				}
			}).collect(Collectors.toList());
		}

		lines = lines.stream().map(line -> {
			for (String deleter : mDeleters) {
				String tempLine = line;
				line = line.replaceAll(deleter, "");
				if (!line.equals(tempLine))
					mIsModified = true;
			}
			for (Modifier modifier : mModifiers) {
				if (line.matches(modifier.getMatcher())) {
					mIsModified = true;
					line = line.replaceAll(modifier.getOlder(), modifier.getNewer());
					break;
				}
			}
			for (Adder adder : mAdders) {
				if (line.matches(adder.getMatcher())) {
					mIsModified = true;
					if (adder.getAddPosition() == AddPosition.AFTER)
						return line + System.getProperty("line.separator") + adder.getAdder();
					else if (adder.getAddPosition() == AddPosition.BEFORE)
						return adder.getAdder() + System.getProperty("line.separator") + line;
				}
			}
			return line;
		}).collect(Collectors.toList());

		// 如果修改了原文件的内容，那么写入原文件
		if (mIsModified) {
			// 将修改的内容写入文件
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(mFileName))) {
				for (String line : lines) {
					writer.write(line + System.getProperty("line.separator"));
				}
			}
		}
	}

	private class Modifier {
		private String	matcher;
		private String	older;
		private String	newer;

		public Modifier(String matcher, String older, String newer) {
			setMatcher(matcher);
			setOlder(older);
			setNewer(newer);
		}

		public String getMatcher() {
			return matcher;
		}

		public void setMatcher(String matcher) {
			this.matcher = matcher;
		}

		public String getOlder() {
			return older;
		}

		public void setOlder(String older) {
			this.older = older;
		}

		public String getNewer() {
			return newer;
		}

		public void setNewer(String newer) {
			this.newer = newer;
		}
	}

	public enum AddPosition {
		BEFORE, AFTER
	}

	private class Adder {

		private String		matcher;
		private String		adder;
		private String		exister;
		private AddPosition	addPosition;

		public Adder(String matcher, String adder, String exister, AddPosition addPosition) {
			setMatcher(matcher);
			setAdder(adder);
			setExister(exister);
			setAddPosition(addPosition);
		}

		public String getMatcher() {
			return matcher;
		}

		public void setMatcher(String matcher) {
			this.matcher = matcher;
		}

		public String getAdder() {
			return adder;
		}

		public void setAdder(String adder) {
			this.adder = adder;
		}

		public String getExister() {
			return exister;
		}

		public void setExister(String exister) {
			this.exister = exister;
		}

		public AddPosition getAddPosition() {
			return addPosition;
		}

		public void setAddPosition(AddPosition addPosition) {
			this.addPosition = addPosition;
		}

	}
}
