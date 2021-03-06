package time;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import util.ReadFilePath;
import util.SUTimeTool;

/**
 * 
 * @author Lynn
 * @date: 2017/3/6
 * 1.仅对object进行时间信息的判断
 * 2.资源输出的是URI，predicate输出的是的local name
 * 3.是否具有时间信息是对literal或URI字段的进行判断
 */
//C:\Users\Lynn\Desktop\Academic\LinkedDataProject\DataSet\SWCC\workshops\sdow-2008-complete.rdf    28kb
public class ExtractionTime4 {
	public static void main(String[] args) throws IOException{
	    long timeBegin =  System.currentTimeMillis();
	    String filePathInput = "";
	    String fileName = "";
	    Scanner sc = new Scanner(System.in);
	    //从控制台输入路径中读取文件，可以改成读取某dir下的所有文件
	    do {
	      System.out.println("Please enter the file path:");
	      /*filePathInput = Console.readLine()*/
	      filePathInput = sc.nextLine();
	      System.out.println("Please enter the marked file name");
	      fileName= sc.nextLine();
	     } while (!ReadFilePath.filePathValidation(filePathInput));
	    sc.close();
	    
	    File markedFile = new File("D:\\markedFile\\" + fileName + ".txt");
	    FileWriter  outStream= new FileWriter(markedFile); 
	    
		ArrayList<String> pathList = ReadFilePath.readDir(filePathInput);
		Iterator<String> iter = pathList.iterator();
	   //使用Jena的FileManager查找文件，可以用来从文件系统中加载RDF文件，导入至已存在的Model或者创建新的Model
		Model model = ModelFactory.createDefaultModel();
	   //i- indicate the num of file has been read; j - indicate the column of statement 
		int i = 0;
	   //colunm Number
		int j =0;
		while(iter.hasNext()){
			i++;
		String inputFileName = iter.next();	
		System.out.println("**********************fileName********\n" + inputFileName + "\n\n");
		InputStream in = FileManager.get().open(inputFileName);
	    if(in == null){
		   throw new IllegalArgumentException("File" + inputFileName + "not found");
	      }
	    //将RDF文件读入至Model中
	    model.read(in, null);
	    //第一种标准输出，第二种输出到文件中
	   StmtIterator iterator = model.listStatements();
	   while(iterator.hasNext()){
		   j++;
		   org.apache.jena.rdf.model.Statement statement = iterator.nextStatement();
		   Resource subject = statement.getSubject();
		   Property predicate = statement.getPredicate();
		   RDFNode object = statement.getObject(); 
		   System.out.println("subject local name " + subject.toString() +"\n");
		   outStream.write(j + "  " + subject.toString() + "     ");
		   outStream.write(predicate.getLocalName() + "     "); 
		   
		   //RDFNode是包含Resource和literal的接口，可以用来判断一个object是resource、literal或者是blank node
		   if(object instanceof Resource){
			   if(SUTimeTool.SUTimeJudgeFunc(object.toString())){
				   System.out.println("Object Local name" + object.toString());
			       //object带有时间信息则做上标记，输出
				   outStream.write("<br>" + object.toString() + "<br/>\r\n\n");
			   }else{
				   outStream.write(object.toString() + "\r\n\n");
			   }
		    }else{//object是literal
			   if(SUTimeTool.SUTimeJudgeFunc(object.toString())){
			       //object带有时间信息则做上标记，输出
				   outStream.write("<br>" + object.toString() + "<br/>\r\n\n");
			   }else{
				   outStream.write(object.toString() + "\r\n\n");
		          }
	        }
	    }
	  }
	     System.out.println("\n\n#############################File" + i + "#############################\n\n");
		 long timeEnd =  System.currentTimeMillis();
		 System.out.println("Num of file has been read " + i + "\n");
		 System.out.println("time total cost: " + (timeEnd - timeBegin)/1000.0/60.0 + "min\n"); 
		 outStream.close();
	}
}
