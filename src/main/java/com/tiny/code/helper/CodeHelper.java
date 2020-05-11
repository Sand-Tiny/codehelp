/* 
 * 创建日期 2014-7-3
 *
 * 成都天和软件公司
 * 电话：028-85425861 
 * 传真：028-85425861-8008 
 * 邮编：610041 
 * 地址：成都市武侯区航空路6号丰德万瑞中心B座1001 
 * 版权所有
 */
package com.tiny.code.helper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * 代码生成器
 * 
 * 
 * @version 1.0
 */
public class CodeHelper {

	/** 字符编码 */
	private static final String CHARSET = "UTF-8";

	/** 默认配置文件路径 */
	private static final String properties = "code-helper.properties";

	private static final String SQL_TEMPLATE = "sql_template.xml";

	private static final String BEAN_TEMPLATE = "bean_template.xml";

	private static final String DAO_TEMPLATE = "dao_template.xml";

	private static final String SERVICE_TEMPLATE = "service_template.xml";

	private static final String MYBATIS_TEMPLATE = "mybatis_template.xml";

	private JFrame frame = new JFrame("Tiny代码工具 小波 sand_tiny@qq.com");

	private JTextField driverField = new JTextField();

	private JTextField urlField = new JTextField();

	private JTextField usernameField = new JTextField();

	private JTextField passwordField = new JTextField();

	private JTextField databaseField = new JTextField();

	private JTextField tableField = new JTextField();

	private JTextField packageField = new JTextField();

	private JTextField authorField = new JTextField();

	/** 生成代码 */
	private JButton codeButton = new JButton("生成代码");

	private JTextField pathField = new JTextField();

	private JButton fileButton = new JButton("生成代码文件");

	private JTextArea beanText = new JTextArea();

	private JTextArea mybatisText = new JTextArea();

	private JTextArea daoText = new JTextArea();

	private JTextArea serviceText = new JTextArea();

	static {
		// 设置默认字体样式
		FontUIResource fontRes = new FontUIResource(new Font("微软雅黑 Linght", 1, 14));
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource)
				UIManager.put(key, fontRes);
		}
	}

	public CodeHelper() throws Exception {
		init();
		createView();
	}

	/***
	 * 初始化
	 * 
	 * @throws Exception
	 */
	private void init() throws Exception {
		Properties p = new Properties();
		String text = Util.read(properties);
		p.load(new StringReader(text));
		driverField.setText(p.getProperty("driver"));
		urlField.setText(p.getProperty("url"));
		usernameField.setText(p.getProperty("username"));
		passwordField.setText(p.getProperty("password"));
		databaseField.setText(p.getProperty("database"));
		tableField.setText(p.getProperty("table"));
		packageField.setText(p.getProperty("package"));
		authorField.setText(p.getProperty("author"));
	}

	/**
	 * 创建UI
	 * 
	 * 
	 */
	private void createView() {
		final JTabbedPane tab = new JTabbedPane();
		JPanel topPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(topPanel, BoxLayout.Y_AXIS);
		topPanel.setLayout(boxLayout);
		topPanel.add(getField("驱动", driverField));
		topPanel.add(getField("URL", urlField));
		topPanel.add(getField("用户名", usernameField));
		topPanel.add(getField("密码", passwordField));
		topPanel.add(getField("数据库", databaseField));
		topPanel.add(getField("数据库表", tableField));
		topPanel.add(getField("包路径", packageField));
		topPanel.add(getField("作者", authorField));
		topPanel.add(getField("", codeButton));
		topPanel.add(getField("项目路径", pathField));
		topPanel.add(getField("", fileButton));

		topPanel.setPreferredSize(new Dimension(0, 400));
		mybatisText.setBorder(BorderFactory.createEtchedBorder());
		beanText.setBorder(BorderFactory.createEtchedBorder());
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(tab, BorderLayout.CENTER);
		JScrollPane beanScroll = new JScrollPane(beanText);
		final JScrollBar beanScrollBar = beanScroll.getVerticalScrollBar();
		beanScrollBar.setUnitIncrement(100);
		tab.addTab("Bean 代码", beanScroll);
		tab.addTab("Dao 接口", new JScrollPane(daoText));
		tab.addTab("Service 实现", new JScrollPane(serviceText));
		tab.addTab("MyBatis 配置", new JScrollPane(mybatisText));
		frame.setLayout(new BorderLayout());
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(centerPanel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		codeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				try {
					createCode();
				} catch (Exception e) {
					e.printStackTrace();
					beanText.setText(Util.getStack(e));
				} finally {
					tab.setSelectedIndex(0);
					beanScrollBar.setValue(beanScrollBar.getMinimum());
				}
			}
		});
		fileButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				try {
					writeCode();
				} catch (Exception e) {
					e.printStackTrace();
					beanText.setText(Util.getStack(e));
				} finally {
					tab.setSelectedIndex(0);
					beanScrollBar.setValue(beanScrollBar.getMinimum());
				}
			}
		});
		beanText.setLineWrap(true);// 激活自动换行功能
		mybatisText.setLineWrap(true);// 激活自动换行功能
	}

	/**
	 * 取得字段
	 * 
	 * 
	 * @param title
	 * @param c
	 * @return 标签 + 输入框
	 */
	private JPanel getField(String title, JComponent c) {
		JPanel tr = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel(title);
		label.setPreferredSize(new Dimension(80, 30));
		tr.add(label);
		tr.add(c);
		c.setPreferredSize(new Dimension(600, 30));
		return tr;
	}

	/**
	 * 生成代码
	 * 
	 * 
	 */
	private void createCode() throws Exception {
		Properties p = new Properties();
		p.put("driver", driverField.getText());
		p.put("url", urlField.getText());
		p.put("username", usernameField.getText());
		p.put("password", passwordField.getText());
		p.put("table", tableField.getText());
		p.put("package", packageField.getText());
		p.put("author", authorField.getText());
		p.put("database", databaseField.getText());
		List<Column> columns = getColumns(p, tableField.getText());
		Table table = getTable(p, tableField.getText());
		mybatisText.setText("");
		beanText.setText("");
		daoText.setText("");
		serviceText.setText("");
		String packages = p.getProperty("package");
		String author = p.getProperty("author");
		if (!columns.isEmpty()) {
			String mybatisCode = getMyBatisCode(table, packages, author, columns);
			mybatisText.setText(mybatisCode);
			String domainCode = getBeanCode(table, packages, author, columns);
			beanText.setText(domainCode);
			String serviceCode = getServiceCode(table, packages, author, columns.get(0));
			serviceText.setText(serviceCode);
			String daoCode = getDaoCode(table, packages, author, columns.get(0));
			daoText.setText(daoCode);
		}
	}

	/**
	 * 生成代码
	 * 
	 * 
	 */
	@SuppressWarnings("resource")
	private void writeCode() throws Exception {
		Properties p = new Properties();
		p.put("driver", driverField.getText());
		p.put("url", urlField.getText());
		p.put("username", usernameField.getText());
		p.put("password", passwordField.getText());
		p.put("table", tableField.getText());
		p.put("package", packageField.getText());
		p.put("author", authorField.getText());
		p.put("database", databaseField.getText());
		p.put("path", pathField.getText());
		Table table = getTable(p, tableField.getText());
		String packages = p.getProperty("package");
		String path = p.getProperty("path") + "src/main/java/";
		if (packages != null) {
			for (String tp : packages.split("\\.")) {
				path = path + tp + "/";
			}
		}
		String className = Util.firstCharUpperCase(Util.toFieldName(table.getName()));
		String mybatisCode = mybatisText.getText();
		File file = new File(path + "dao/mapping/" + p.getProperty("table") + ".xml");
		file.getParentFile().mkdirs();
		file.createNewFile();
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(mybatisCode);
		fileWriter.flush();

		String domainCode = beanText.getText();
		file = new File(path + "entity/" + className + ".java");
		file.getParentFile().mkdirs();
		file.createNewFile();
		fileWriter = new FileWriter(file);
		fileWriter.write(domainCode);
		fileWriter.flush();

		String serviceCode = serviceText.getText();
		file = new File(path + "service/" + className + "Service.java");
		file.getParentFile().mkdirs();
		file.createNewFile();
		fileWriter = new FileWriter(file);
		fileWriter.write(serviceCode);
		fileWriter.flush();

		String daoCode = daoText.getText();
		file = new File(path + "dao/" + className + "Dao.java");
		file.getParentFile().mkdirs();
		file.createNewFile();
		fileWriter = new FileWriter(file);
		fileWriter.write(daoCode);
		fileWriter.flush();
		fileWriter.close();
	}

	/**
	 * 生成MyBatis代码
	 * 
	 * 
	 * @param table
	 * @param pack
	 * @param author
	 * @param columns
	 * @throws Exception
	 */
	private String getMyBatisCode(Table table, String pack, String author, List<Column> columns) throws Exception {
		String template = Util.read(MYBATIS_TEMPLATE);
		String className = Util.firstCharLowerCase(Util.toFieldName(table.getName()));
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("table.name", table.getName());
		map.put("table.desc", table.getDesc());
		map.put("class.daoName", pack + ".dao." + Util.firstCharUpperCase(className) + "Dao");
		map.put("class.name", className);
		map.put("class.full", pack + ".entity." + Util.firstCharUpperCase(className));
		map.put("author", author);
		Column idColumn = columns.get(0);
		map.put("id.column", "`" + idColumn.getName() + "`");
		map.put("id.field", idColumn.getField());
		List<String> insertFields = new ArrayList<String>();
		List<String> batchInsertFields = new ArrayList<String>();
		List<String> insertColumns = new ArrayList<String>();
		List<String> columnsSelect = new ArrayList<String>();
		List<String> columnsUpdate = new ArrayList<String>();
		List<String> columnsMapping = new ArrayList<String>();
		List<String> queryWhere = new ArrayList<String>();
		for (int i = 0; i < columns.size(); i++) {
			Column c = columns.get(i);
			if (i != 0) {
				insertColumns.add("`" + c.getName() + "`");
				insertFields.add("#{" + c.getField() + "}");
				batchInsertFields.add("#{" + className + "." + c.getField() + "}");
				columnsUpdate.add("`" + c.getName() + "`" + " = #{" + c.getField() + "}");
			}
			columnsSelect.add("`" + c.getName() + "`");
			columnsMapping.add("		<result property=\"" + c.getField() + "\"                    column=\""
					+ c.getName() + "\"/>\n");
			queryWhere.add("<if test=\"" + c.getField() + " != null\">\n                and `" + c.getName() + "` = #{"
					+ c.getField() + "}\n            </if>");
		}
		map.put("columns.insert", Util.join(insertColumns, ",\n            "));
		map.put("fields.insert", Util.join(insertFields, ",\n            "));
		map.put("fields.batchInsert", Util.join(batchInsertFields, ",\n            "));
		map.put("columns.select", Util.join(columnsSelect, ",\n            "));
		map.put("columns.update", Util.join(columnsUpdate, ",\n            "));
		map.put("columns.query", Util.join(queryWhere, "\n            "));
		map.put("columns.mapping", Util.join(columnsMapping, ""));
		for (Map.Entry<String, String> entry : map.entrySet()) {
			template = template.replace("#" + entry.getKey() + "#", entry.getValue());
		}
		return template;
	}

	/**
	 * 生成实体代码
	 * 
	 * 
	 * @param table
	 * @param pack
	 * @param columns
	 * @throws Exception
	 */
	private String getBeanCode(Table table, String pack, String author, List<Column> columns) throws Exception {
		String xml = Util.read(BEAN_TEMPLATE);
		String classTemplate = Util.matchs(xml, "<class>([\\w\\W]+?)</class>", 1).get(0);
		String fieldTemplate = Util.matchs(xml, "<field>([\\w\\W]+?)</field>", 1).get(0);
		String methodTemplate = Util.matchs(xml, "<method>([\\w\\W]+?)</method>", 1).get(0);
		String className = Util.firstCharUpperCase(Util.toFieldName(table.getName()));
		StringBuilder fields = new StringBuilder();
		for (Column c : columns) {
			String template = fieldTemplate;
			Map<String, String> fieldMap = new LinkedHashMap<String, String>();
			fieldMap.put("field.col", c.getName());
			fieldMap.put("field.name", c.getField());
			fieldMap.put("field.length", String.valueOf(c.getLength()));
			fieldMap.put("field.nullable", String.valueOf(c.isNullable()));
			fieldMap.put("field.desc", String.valueOf(c.getDesc()));
			fieldMap.put("field.type", c.getFieldType());
			for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
				template = template.replace("#" + entry.getKey() + "#", entry.getValue());
			}
			fields.append(template);
		}
		StringBuilder methods = new StringBuilder();
		for (Column c : columns) {
			String template = methodTemplate;
			Map<String, String> fieldMap = new LinkedHashMap<String, String>();
			fieldMap.put("method.get", "get" + Util.firstCharUpperCase(c.getField()));
			fieldMap.put("method.set", "set" + Util.firstCharUpperCase(c.getField()));
			fieldMap.put("field.name", c.getField());
			fieldMap.put("field.desc", Util.isEmpty(c.getDesc()) ? c.getField() : String.valueOf(c.getDesc()));
			fieldMap.put("field.type", c.getFieldType());
			for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
				template = template.replace("#" + entry.getKey() + "#", entry.getValue());
			}
			methods.append(template);
		}
		Map<String, String> classMap = new LinkedHashMap<String, String>();
		classMap.put("table.name", table.getName());
		classMap.put("table.desc", table.getDesc());
		classMap.put("class.name", className);
		classMap.put("class.package", pack);
		classMap.put("class.full", pack + "." + className);
		classMap.put("fields", fields.toString());
//        classMap.put("methods", methods.toString());
		classMap.put("methods", "");
		classMap.put("now", Util.format(new Date()));
		classMap.put("author", author);
		for (Map.Entry<String, String> entry : classMap.entrySet()) {
			classTemplate = classTemplate.replace("#" + entry.getKey() + "#", entry.getValue());
		}
		return classTemplate.toString();
	}

	/**
	 * 生成Service代码
	 * 
	 * 
	 * @param table
	 * @param pack
	 * @param idColumn
	 * @return
	 * @throws Exception
	 */
	private String getDaoCode(Table table, String pack, String author, Column idColumn) throws Exception {
		String xml = Util.read(DAO_TEMPLATE);
		String serviceTemplate = Util.matchs(xml, "<class>([\\w\\W]+?)</class>", 1).get(0);
		String className = Util.firstCharUpperCase(Util.toFieldName(table.getName()));
		Map<String, String> classMap = new LinkedHashMap<String, String>();
		classMap.put("table.name", table.getName());
		classMap.put("table.desc", table.getDesc());
		classMap.put("class.upname", className);
		classMap.put("class.name", Util.firstCharLowerCase(className));
		classMap.put("class.package", pack);
		classMap.put("class.full", pack + ".entity." + className);
		classMap.put("id.field", idColumn.getField());
		classMap.put("now", Util.format(new Date()));
		classMap.put("author", author);
		for (Map.Entry<String, String> entry : classMap.entrySet()) {
			serviceTemplate = serviceTemplate.replace("#" + entry.getKey() + "#", entry.getValue());
		}
		return serviceTemplate.toString();
	}

	/**
	 * 生成ServiceImpl代码
	 * 
	 * 
	 * @param table
	 * @param pack
	 * @param idColumn
	 * @return
	 * @throws Exception
	 */
	private String getServiceCode(Table table, String pack, String author, Column idColumn) throws Exception {
		String xml = Util.read(SERVICE_TEMPLATE);
		String serviceTemplate = Util.matchs(xml, "<class>([\\w\\W]+?)</class>", 1).get(0);
		String className = Util.firstCharUpperCase(Util.toFieldName(table.getName()));
		Map<String, String> classMap = new LinkedHashMap<String, String>();
		classMap.put("table.name", table.getName());
		classMap.put("table.desc", table.getDesc());
		classMap.put("class.upname", className);
		classMap.put("class.name", Util.firstCharLowerCase(className));
		classMap.put("class.package", pack);
		classMap.put("class.full", pack + ".entity." + className);
		classMap.put("id.field", idColumn.getField());
		classMap.put("now", Util.format(new Date()));
		classMap.put("author", author);
		for (Map.Entry<String, String> entry : classMap.entrySet()) {
			serviceTemplate = serviceTemplate.replace("#" + entry.getKey() + "#", entry.getValue());
		}
		return serviceTemplate.toString();
	}

	/**
	 * 取得表的数据列
	 * 
	 * 
	 * @param p         参数
	 * @param tableName 表
	 * @return 数据列
	 * @throws Exception
	 */
	private List<Column> getColumns(Properties p, String tableName) throws Exception {
		String xml = Util.read(SQL_TEMPLATE);
		String sql = Util.matchs(xml, "<column>([\\w\\W]+?)</column>", 1).get(0);
		sql = sql.replace("#table#", tableName);
		sql = sql.replace("#database#", p.getProperty("database"));
		Class.forName(p.getProperty("driver"));
		Connection conn = null;
		ResultSet rs = null;
		List<Column> rows = new ArrayList<Column>();
		try {
			conn = DriverManager.getConnection(p.getProperty("url"), p.getProperty("username"),
					p.getProperty("password"));
			rs = conn.prepareStatement(sql.toString()).executeQuery();
			while (rs.next()) {
				Column col = new Column(rs);
				rows.add(col);
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		return rows;
	}

	/**
	 * 取得表信息
	 * 
	 * 
	 * @param p         参数
	 * @param tableName 表名称
	 * @return
	 * @throws Exception
	 */
	private Table getTable(Properties p, String tableName) throws Exception {
		String xml = Util.read(SQL_TEMPLATE);
		String sql = Util.matchs(xml, "<table>([\\w\\W]+?)</table>", 1).get(0);
		sql = sql.replace("#table#", tableName);
		sql = sql.replace("#database#", p.getProperty("database"));
		Connection conn = null;
		ResultSet rs = null;
		Table table = new Table(tableName, tableName);
		try {
			Class.forName(p.getProperty("driver"));
			conn = DriverManager.getConnection(p.getProperty("url"), p.getProperty("username"),
					p.getProperty("password"));
			rs = conn.prepareStatement(sql.toString()).executeQuery();
			while (rs.next()) {
				table = new Table(tableName, rs.getString("table_desc"));
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		return table;
	}

	/** 数据列定义 */
	static class Column {

		private String name;

		private String desc;

		private String type;

		private boolean nullable;

		private String length;

		public Column(ResultSet rs) throws Exception {
			this.name = rs.getString("name");
			this.desc = rs.getString("desc");
			this.type = rs.getString("type");
			this.nullable = rs.getBoolean("nullable");
			String length = rs.getString("length");
			this.length = length != null ? length : "0";
		}

		public Column(String name, String desc, String type, boolean nullable, String length) {
			this.name = name;
			this.desc = desc;
			this.type = type;
			this.nullable = nullable;
			this.length = length;
		}

		public String getName() {
			return name;
		}

		public String getDesc() {
			return desc;
		}

		public String getType() {
			return type;
		}

		public boolean isNullable() {
			return nullable;
		}

		public String getLength() {
			return length;
		}

		public String getField() {
			return Util.toFieldName(name);
		}

		public String getFieldType() {
			type = type.toLowerCase();
			if (type.contains("char") || type.contains("text")) {
				return "String";
			} else if (type.equals("int") || type.equals("tinyint")) {
				return "Integer";
			} else if (type.contains("bigint") || type.contains("long") || type.contains("number")) {
				return "Long";
			} else if (type.contains("double")) {
				return "Double";
			} else if (type.contains("date") || type.contains("time")) {
				return "Date";
			} else if (type.contains("decimal")) {
				return "BigDecimal";
			}
			return "unknown";
		}

		@Override
		public String toString() {
			return "{name=" + name + ", title=" + desc + ", type=" + type + ", nullable=" + nullable + ", length="
					+ length + "}";
		}
	}

	/** 表定义 */
	static class Table {

		private String name;

		private String desc;

		public Table(String name, String desc) {
			this.name = name;
			this.desc = desc;
		}

		public String getName() {
			return name;
		}

		public String getDesc() {
			return desc;
		}

		@Override
		public String toString() {
			return "Table [name=" + name + ", desc=" + desc + "]";
		}
	}

	/** 工具类 */
	static class Util {

		/** 格式化日期 */
		public static String format(Date date) {
			if (date == null) {
				return null;
			}
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(date);
		}

		/**
		 * 取得匹配的字符串
		 * 
		 * 
		 * @param input 字符串
		 * @param regex 正则表达式
		 * @param group 正则分组1-9
		 * @return List 匹配的字符串
		 */
		public static List<String> matchs(String input, String regex, int group) {
			Pattern pattern = Pattern.compile(regex);
			Matcher match = pattern.matcher(input);
			List<String> matches = new ArrayList<String>();
			while (match.find()) {
				matches.add(match.group(group));
			}
			return matches;
		}

		/**
		 * 首字母大写
		 * 
		 * 
		 * @param value 对象值
		 * @return String 字符串
		 */
		public static String firstCharUpperCase(String value) {
			if (isValid(value)) {
				return value.substring(0, 1).toUpperCase() + value.substring(1);
			}
			return value;
		}

		/**
		 * 首字母小写
		 * 
		 * 
		 * @param value 对象值
		 * @return String 字符串
		 */
		public static String firstCharLowerCase(String value) {
			if (isValid(value)) {
				return value.substring(0, 1).toLowerCase() + value.substring(1);
			}
			return value;
		}

		/**
		 * 转换格式 CUST_INFO_ID - > custInfoId
		 * 
		 * 
		 * @param name
		 * @return
		 */
		public static String toFieldName(String name) {
			if (name == null) {
				return null;
			}
			String field = name.toLowerCase();
			String[] values = field.split("\\_");
			StringBuffer b = new StringBuffer(name.length());
			for (int i = 0; i < values.length; i++) {
				if (i == 0)
					b.append(values[i]);
				else
					b.append(firstCharUpperCase(values[i]));
			}
			return b.toString();
		}

		/**
		 * 连接字符串
		 * 
		 * 
		 * @param list  列表
		 * @param split 分隔符
		 * @param wrap  包裹字符
		 * @return 字符串
		 */
		public static <T> String join(List<String> list, String split) {
			if (list == null)
				return null;
			String[] array = list.toArray(new String[] {});
			StringBuilder s = new StringBuilder(128);
			for (int i = 0; i < array.length; i++) {
				if (i > 0) {
					s.append(split);
				}
				s.append(array[i]);
			}
			return s.toString();
		}

		/**
		 * 判断字符串是否为Null或trim后长度为0
		 * 
		 * 
		 * @param validate
		 * @return
		 */
		public static boolean isEmpty(Object value) {
			if (value == null)
				return true;
			return value.toString().trim().isEmpty();
		}

		/**
		 * 判断对象是否有效
		 * 
		 * 
		 * @param value
		 * @return
		 */
		public static boolean isValid(Object value) {
			return !isEmpty(value);
		}

		/**
		 * 读取文件
		 * 
		 * 
		 * @param file 文件
		 * @return 文件内容
		 */
		public static String read(String file) throws Exception {
			InputStream in = null;
			InputStreamReader reader = null;
			try {
				in = CodeHelper.class.getResource(file).openStream();
				reader = new InputStreamReader(in, CHARSET);
				StringWriter writer = new StringWriter();
				int len = -1;
				char[] buffer = new char[128];
				while ((len = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, len);
				}
				writer.flush();
				return writer.toString();
			} finally {
				if (reader != null)
					reader.close();
			}
		}

		/**
		 * 取得异常信息
		 * 
		 * 
		 * @param e
		 * @return
		 */
		public static String getStack(Exception e) {
			Writer writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		}
	}

	/**
	 * 启动代码服务
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new CodeHelper();
	}
}
