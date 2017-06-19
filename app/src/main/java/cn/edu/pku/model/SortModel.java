package cn.edu.pku.model;

public class SortModel {

	/**
	 * 显示名称
	 */
	private String name;

	/**
	 * 拼音的首字母
	 */
	private String sortLetters;

	/**
	 * 城市代码，以便正确返回城市代码
	 */
	private String code;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
