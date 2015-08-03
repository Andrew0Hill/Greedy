package tcc.com.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import tcc.com.cluster.Cluster;
import tcc.com.clustering.Clustering;
import tcc.com.graph.Edge;
import tcc.com.graph.NameGraph;
import tcc.com.graph.VertName;

public class Util {
	//script q le um xml especifico
	static void xmlToXls() throws IOException, SQLException, ClassNotFoundException{
		HSSFWorkbook workbook2 = new HSSFWorkbook();
		HSSFSheet sheet = workbook2.createSheet("sample");

		List<String[]> lista = new ArrayList<String[]>();
		BufferedReader reader = new BufferedReader(new FileReader("fahl-labeled"));
		String line = null;
		String[] tupla = new String[13];
		int i = 0;

		while((line = reader.readLine()) != null){
			if(!line.isEmpty()){
				tupla = new String[13];
				Row row = sheet.createRow(i++);

				Pattern author = Pattern.compile("<author>(.*?)</author>");
				Pattern volume = Pattern.compile("<volume>(.*?)</volume>");
				Pattern title = Pattern.compile("<title>(.*?)</title>");
				Pattern institution = Pattern.compile("<institution>(.*?)</institution>");
				Pattern venue = Pattern.compile("<booktitle>(.*?)</booktitle>");//booktitle
				Pattern address = Pattern.compile("<address>(.*?)</address>");
				Pattern publisher = Pattern.compile("<publisher>(.*?)</publisher>");
				Pattern year = Pattern.compile("<date>(.*?)</date>");//date
				Pattern pages = Pattern.compile("<pages>(.*?)</pages>");
				Pattern editor = Pattern.compile("<editor>(.*?)</editor>");
				Pattern note = Pattern.compile("<note>(.*?)</note>");
				Pattern classe = Pattern.compile("(.*?)<author>");//antes de author

				String autor = "";
				String vol = "";
				String titl = "";
				String inst = "";
				String ven = "";
				String addr = "";
				String publi = "";
				String pgs = "";
				String edit = "";
				String nota = "";
				String clas = "";
				String date = "";
				String yr = "";
				String month = "";

				Matcher matchAuth = author.matcher(line);
				if(matchAuth.find()){
					autor = matchAuth.group(1);
				}

				Matcher matchVol = volume.matcher(line);
				if(matchVol.find()){
					vol = matchVol.group(1);
				}

				Matcher matchTitle = title.matcher(line);
				if(matchTitle.find()){
					titl = matchTitle.group(1);
				}

				Matcher matchInst = institution.matcher(line);
				if(matchInst.find()){
					inst = matchInst.group(1);
				}

				Matcher matchVen = venue.matcher(line);
				if(matchVen.find()){
					ven = matchVen.group(1);
				}

				Matcher matchAddr = address.matcher(line);
				if(matchAddr.find()){
					addr = matchAddr.group(1);
				}

				Matcher matchPubl = publisher.matcher(line);
				if(matchAuth.find()){
					publi = matchPubl.group(1);
				}

				Matcher matchPgs = pages.matcher(line);
				if(matchPgs.find()){
					pgs = matchPgs.group(1);
				}

				Matcher matchEd = editor.matcher(line);
				if(matchEd.find()){
					edit = matchEd.group(1);
				}

				Matcher matchNt = note.matcher(line);
				if(matchNt.find()){
					nota = matchNt.group(1);
				}

				Matcher matchClass = classe.matcher(line);
				if(matchClass.find()){
					clas = matchClass.group(1);
				}

				Matcher matchDate = year.matcher(line);
				while (matchDate.find())
				{
					if(!date.isEmpty()){
						date += ";"+matchDate.group(1);
					}else{
						date += matchDate.group(1);
					}
				}
				String[] mtYr = date.split(";");
				if(mtYr.length>1){
					month = mtYr[0];
					yr = mtYr[1];

				}else{
					String[] mtYr2 = mtYr[0].split(" ");
					if(mtYr2.length > 1){
						month = mtYr2[0];
						yr = mtYr2[1];
					}else{
						if(mtYr2[0].length()>=5 && mtYr2[0].length()<=7){
							yr = mtYr2[0];
						}else{
							month = mtYr2[0];
						}
					}
				}

				autor = autor.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "")
						.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", "");
				vol = vol.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "")
						.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", "");
				titl = titl.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "")
						.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", "");
				inst = inst.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "")
						.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", "");
				ven = ven.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "")
						.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", "");
				addr = addr.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "")
						.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", "");
				publi = publi.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "")
						.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", "");
				pgs = pgs.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "")
						.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", "");
				edit = edit.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "")
						.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", "");
				nota = nota.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "")
						.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", "");
				clas = clas.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "")
						.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", "");
				date = date.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "")
						.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", "");
				yr = yr.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "").replace("[", "")
						.replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "")
						.replace(";", "");
				month = month.replace("\'", "").replace(":", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "").replace("[", "")
						.replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace("+", "").replace("\\", "").replace("//", "").replace(";", ""); 

				Cell cell = row.createCell(0);
				Cell cell1 = row.createCell(1);
				Cell cell2 = row.createCell(2);
				Cell cell3 = row.createCell(3);
				Cell cell4 = row.createCell(4);
				Cell cell5 = row.createCell(5);
				Cell cell6 = row.createCell(6);
				Cell cell7 = row.createCell(7);
				Cell cell8 = row.createCell(8);
				Cell cell9 = row.createCell(9);
				Cell cell10 = row.createCell(10);
				Cell cell11 = row.createCell(11);
				Cell cell12 = row.createCell(12);

				cell.setCellValue(autor);
				cell1.setCellValue(vol);
				cell2.setCellValue(titl);
				cell3.setCellValue(inst);
				cell4.setCellValue(ven);
				cell5.setCellValue(addr);
				cell6.setCellValue(publi);
				cell7.setCellValue(yr);
				cell8.setCellValue(pgs);
				cell9.setCellValue(edit);
				cell10.setCellValue(nota);
				cell11.setCellValue(month);
				cell12.setCellValue(clas);

				tupla[0] = autor;
				tupla[1] = vol;
				tupla[2] = titl;
				tupla[3] = inst;
				tupla[4] = ven;
				tupla[5] = addr;
				tupla[6] = publi;
				tupla[8] = pgs;
				tupla[9] = edit;
				tupla[10] = nota;
				tupla[12] = clas;
				tupla[7] = yr;
				tupla[11] = month;

				lista.add(tupla);

			}
		}

		FileOutputStream out = new FileOutputStream(new File("fahl-labeled.xls"));
		workbook2.write(out);
		out.close();
		insert(lista);
	}

	static void genXls() throws IOException, SQLException, ClassNotFoundException{

		List<String[]> lista = new ArrayList<String[]>();
		BufferedReader reader = new BufferedReader(new FileReader("cora2.txt"));
		String line = null;
		String[] tupla;
		while((line = reader.readLine()) != null){
			tupla = line.split(";");
			lista.add(tupla);
		}
		insert(lista);

		reader.close();
		String[] linha;

		HSSFWorkbook workbook2 = new HSSFWorkbook();
		HSSFSheet sheet = workbook2.createSheet("sample");
		for(int i = 0; i < lista.size();i++){
			linha =lista.get(i);
			Row row = sheet.createRow(i);
			for(int j=0; j< linha.length; j++){

				Cell cell = row.createCell(j);
				cell.setCellValue(linha[j].replace("\'", "")
						.replace(":", "")
						//						.replace(",", "")
						//						.replace(".", "")
						.replace("(", "")
						.replace(")", "")
						.replace("[", "")
						.replace("]", "")
						.replace("!", "")
						.replace("?", "")
						.replace("-", "")
						.replace("+", "")
						.replace("\\", "")
						.replace("//", ""));
				//						.replace(";", ""));
			}

		}
		FileOutputStream out = new FileOutputStream(new File("newcora.xls"));
		workbook2.write(out);
		out.close();
	}

	static void insert(List<String[]> lista) throws SQLException, ClassNotFoundException{

		// create a mysql database connection
		String myDriver = "com.mysql.jdbc.Driver";
		String myUrl = "jdbc:mysql://localhost/tcc_schema2";
		Class.forName(myDriver);
		Connection conn = DriverManager.getConnection(myUrl, "root", "");

		// create a sql date object so we can use it in our INSERT statement
		//		      Calendar calendar = Calendar.getInstance();
		//		      java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

		// the mysql insert statement
		String query = " insert into book (author,volume,title,institution,venue,address,publisher,year,pages,editor,note,month,class)"
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		// create the mysql insert preparedstatement
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		for(String[] tuple:lista){
			for(int j=0; j< tuple.length; j++){
				//				System.out.println(tuple.length);
				preparedStmt.setString (j+1, tuple[j].replace("\\", "")
						.replace("//", "").replace("\'", ""));
			}
			preparedStmt.addBatch();
		}

		// execute the preparedstatement
		preparedStmt.executeBatch();

		conn.close();
	}

	public static List<List<List<String>>> getDataBD() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		String author,volume,title,institution,venue,address,publisher,year,pages,editor,note,month,classe;
		int cluster, id;
		List<String> lista;
		List<List<String>> listasc1 = new ArrayList<List<String>>();
		List<List<String>> listasc2 = new ArrayList<List<String>>();

		List<List<List<String>>> listareturn = new ArrayList<List<List<String>>>();

		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost/tcc_schema", "root", "");

		java.sql.Statement st = con.createStatement();
		//		String sql = ("SELECT * FROM book where cluster = 0");
		String sql = ("SELECT * FROM book where cluster = 0 and id > 99918531");//and id < 16150   in ('16104','16110','16109','16121','16136','16140')
		java.sql.ResultSet rs = st.executeQuery(sql);
		while(rs.next()) { 
			lista = new ArrayList<String>();
			//		 int id = rs.getInt("first_column_name"); 
			//		 String str1 = rs.getString("second_column_name");
			cluster = rs.getInt("cluster");
			lista.add(String.valueOf(cluster));
			id = rs.getInt("id");
			lista.add(String.valueOf(id));

			author = rs.getString("author");
			lista.add(author);
			volume = rs.getString("volume");
			lista.add(volume);
			title = rs.getString("title");
			lista.add(title);
			institution = rs.getString("institution");
			lista.add(institution);
			venue = rs.getString("venue");
			lista.add(venue);
			address = rs.getString("address");
			lista.add(address);
			publisher = rs.getString("publisher");
			lista.add(publisher);
			year = rs.getString("year");
			lista.add(year);
			pages = rs.getString("pages");
			lista.add(pages);
			editor = rs.getString("editor");
			lista.add(editor);
			note = rs.getString("note");
			lista.add(note);
			month = rs.getString("month");
			lista.add(month);
			classe = rs.getString("class");
			lista.add(classe);

			listasc1.add(lista);
		}
		st.close();
		rs.close();
		con.close();

		con = DriverManager.getConnection("jdbc:mysql://localhost/tcc_schema2", "root", "");
		st = con.createStatement();
		//		String sql = ("SELECT * FROM book where cluster = 0");
		sql = ("SELECT * FROM book where cluster = 0 and id in(18479,18482,18483,18461,18464,18465,18469,18470,18471)");//and id < 18531 and id in(18479,18482,18483,18461,18464,18465,18469,18470,18471)
		rs = st.executeQuery(sql);

		while(rs.next()) { 
			lista = new ArrayList<String>();
			//		 int id = rs.getInt("first_column_name"); 
			//		 String str1 = rs.getString("second_column_name");
			cluster = rs.getInt("cluster");
			lista.add(String.valueOf(cluster));
			id = rs.getInt("id");
			lista.add(String.valueOf(id));

			author = rs.getString("author");
			lista.add(author);
			volume = rs.getString("volume");
			lista.add(volume);
			title = rs.getString("title");
			lista.add(title);
			institution = rs.getString("institution");
			lista.add(institution);
			venue = rs.getString("venue");
			lista.add(venue);
			address = rs.getString("address");
			lista.add(address);
			publisher = rs.getString("publisher");
			lista.add(publisher);
			year = rs.getString("year");
			lista.add(year);
			pages = rs.getString("pages");
			lista.add(pages);
			editor = rs.getString("editor");
			lista.add(editor);
			note = rs.getString("note");
			lista.add(note);
			month = rs.getString("month");
			lista.add(month);
			classe = rs.getString("class");
			lista.add(classe);

			listasc2.add(lista);
		}
		st.close();
		rs.close();
		con.close();

		listareturn.add(listasc1);
		listareturn.add(listasc2);

		return listareturn;
	}

	public static void updateClusterXls(Map<String, Integer> cluster_n) throws IOException{
		//Read the spreadsheet that needs to be updated
		FileInputStream input_document = new FileInputStream(new File("schema1.xls"));
		//Access the workbook
		HSSFWorkbook my_xls_workbook = new HSSFWorkbook(input_document); 
		//Access the worksheet, so that we can update / modify it.
		HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0); 

		// declare a Cell object
		Cell cell = null; 
		Cell cell2 = null;
		// Access the cell first to update the value
		for(Row row : my_worksheet){
			if(row.getRowNum()>0){
				cell = row.getCell(0);//Cluster
				cell2 = row.getCell(1);//ID

				int newClusId = 0;
				String id = String.valueOf((int) cell2.getNumericCellValue());
				if(cluster_n.get(id) != null){
					newClusId = cluster_n.get(id);
					if(newClusId == 35){System.out.println("aa");}
					cell.setCellValue(newClusId);//atualizando o numero do clustes
				}

			}		
		}
		input_document.close();

		//abre FileOutputStream
		FileOutputStream output_file = new FileOutputStream(new File("schema1.xls"));
		//escreve as modificoes
		my_xls_workbook.write(output_file);
		//fecha o stream
		output_file.close();

	}

	public static void updateClusterXls2(Map<String, Integer> cluster_n) throws IOException{
		//Read the spreadsheet that needs to be updated
		FileInputStream input_document = new FileInputStream(new File("schema2.xls"));
		//Access the workbook
		HSSFWorkbook my_xls_workbook = new HSSFWorkbook(input_document); 
		//Access the worksheet, so that we can update / modify it.
		HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0); 

		// declare a Cell object
		Cell cell = null; 
		Cell cell2 = null;
		// Access the cell first to update the value
		for(Row row : my_worksheet){
			if(row.getRowNum()>0){
				cell = row.getCell(0);//Cluster
				cell2 = row.getCell(1);//ID

				int newClusId = 0;
				String id = String.valueOf((int) cell2.getNumericCellValue());
				if(cluster_n.get(id) != null){
					newClusId = cluster_n.get(id);
					if(newClusId == 35){System.out.println("aa");}
					cell.setCellValue(newClusId);//atualizando o numero do clustes
				}

			}		
		}
		input_document.close();

		//abre FileOutputStream
		FileOutputStream output_file = new FileOutputStream(new File("schema2.xls"));
		//escreve as modificoes
		my_xls_workbook.write(output_file);
		//fecha o stream
		output_file.close();

	}

	public static String getIdsBds(String key, List<String> idKeys){
		String retorno = "";
		for(String idKey:idKeys){
			if(idKey.contains(key.split("\t")[0])){
				if(!retorno.isEmpty())
					retorno=retorno.concat(",");
				retorno=retorno.concat(idKey.split("\\t")[0]);
			}
		}

		return retorno;
	}

	public static void writeGraph(Clustering clustering) throws IOException {
		NameGraph grafo = clustering.getGrafo();
		Map<Integer,Edge> edges = grafo.getEdges();
		Map<Integer,VertName> verts = grafo.getVertices();
		List<Cluster> clusters = clustering.getLg();

		File file = new File("graph.csv");

		// se arquivo nao existe, criar
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write("Vertex ID;Record;TokenID;Token Comparacao"+'\n');

		for(Map.Entry<Integer, VertName> vert : verts.entrySet()){
			VertName vertex = vert.getValue();
			bw.write(vertex.getId()+";"+vertex.getTupla()+";"+vertex.getKey()+";"+vertex.getName()+'\n');
		}

		bw.write("EdgeID;VertexID1;VertexID2;Similarity"+'\n');
		for(Map.Entry<Integer,Edge> edg : edges.entrySet()){
			Edge edge = edg.getValue(); 
			bw.write(edge.getId()+";"+edge.getElemone().getId()+";"+edge.getElemtwo().getId()+";"+edge.getWeight()+'\n');
		}

		bw.write("ClusterID;List of Vertexes"+'\n');
		String line = "";
		for(Cluster clus : clusters){
			boolean first = true;
			line = "";

			Map<Integer, VertName> vertices = clus.getVertices();
			for(Map.Entry<Integer,VertName> vrtx : vertices.entrySet()){
				if (first){
					line += vrtx.getValue().getId();
					first = false;
				}else{
					line += ","+vrtx.getValue().getId();
				}
			}

			if(!vertices.isEmpty()){
				bw.write(clus.getId_cluster()+";"+"["+ line +"]"+'\n');
			}
		}

		//iterar por vertice mostrando o id do vertice e a tupla 
		bw.close();

		System.out.println("Grafo Armazenado");

	}

	//	List<String> genIDKeys(List<VertName> vertices){
	//		
	//	}

	//		public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
	//			xmlToXls();
	//		}
//	
//	public static void main(String[] args) throws IOException {
//		writeGraph(null);
//	}
}
