package com.my;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.my.util.JsonUtil;

public class JsonConvert {

	public static void main(String[] args) throws Exception {
		PrintWriter out = new PrintWriter(new FileWriter("E:\\project\\glb-new\\ipb-view.qtlglb.json"));
		
		List<Entity> list = new ArrayList<Entity>();
		
		SAXReader reader = new SAXReader();
		Document document;
		File file = new File("E:\\project\\glb-new\\ipb-view.qtlglb.xml");
		document = reader.read(file);
		final Element root = document.getRootElement();
		
		List<Element> listView = root.element("view_all").elements();
		for (Element e : listView) {
			Entity en = new Entity();
			en.viewName = e.getText();
			en.qos = 80;
			en.list.add("a");
			en.list.add("b");
			list.add(en);
		}
		
		out.print(JsonUtil.toText(list, true, false));
		
		out.close();
		
	}

}

class Entity {
	String viewName;
	int qos;
	List<String> list = new ArrayList<String>();
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	public int getQos() {
		return qos;
	}
	public void setQos(int qos) {
		this.qos = qos;
	}
	public List<String> getList() {
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}
	
	
}
