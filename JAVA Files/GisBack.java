

import java.io.*;
import java.sql.*;  
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Servlet implementation class GisBack
 */
@WebServlet("/gisBack")
public class GisBack extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GisBack() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		Double fromx = Double.parseDouble(request.getParameter("fromx"));
		Double fromy = Double.parseDouble(request.getParameter("fromy"));
		Double destx = Double.parseDouble(request.getParameter("destinationx"));
		Double desty = Double.parseDouble(request.getParameter("destinationy"));
		String sess_name = request.getParameter("u_name");
		int val  = Integer.parseInt(request.getParameter("flag"));
		String x = request.getParameter("x");
		String y = request.getParameter("y");
		//  HttpSession session1=request.getSession(false);
	    //   String uName=(String)session1.getAttribute("uname");
	    //   String Pass=(String)session1.getAttribute("pass");
	       System.out.println(sess_name);
		Connection conn = null; 
		StringBuilder buf = new StringBuilder();
		try{
			Class.forName("org.postgresql.Driver");
			String dburl = "jdbc:postgresql://localhost:5432/postgres";
			conn = DriverManager.getConnection(dburl, "postgres", "admin");
			Statement stat = conn.createStatement();
			//System.out.println("connection-----"+conn);
			//String query = "select * from pgr_fromAtoB('texas_roads_gcs', "+fromx+", "+fromy+", "+destx+","+desty+")"; 
			
			
			
			String query1 = "drop table if exists temproute";
			stat.executeUpdate(query1);
			String query2 = "create table temproute as select * from pgr_fromatob('texas_roads_gcs', "+fromx+", "+fromy+", "+destx+","+desty+")"; 
			stat.executeUpdate(query2);
			String query = "select seq,gid,name,cost,heading from temproute";
			System.out.println(query+"=query pgr from atob");
			//ResultSet rs1 = stat.executeQuery(query1);
			ResultSet rs = stat.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int col = rsmd.getColumnCount();
			buf.append("<html>"+"<body>"+"<table border=2	>"+"<tr>");
			for (int i = 1; i < col; i++) {
				//writer.println("column MetaData ");
				// get the column's name.
				buf.append("<th>"+rsmd.getColumnName(i)+"</th>");
				//writer.println("&emsp;&emsp;");
				
			}
			buf.append("</tr>");
			writer.println("<br/>");
			while(rs.next()){
				buf.append("<tr>");
				for(int i=1;i<col;i++){
					buf.append("<td>"+rs.getString(i)+"</td>"+"&emsp;");
					//writer.println(rs.getString(i));
					//writer.println("&ensp;&ensp;&ensp;");
					//System.out.println(rs.getString(i)+"=value");
				}
				buf.append("</tr>");
				//writer.println("<br/>");
			}
			buf.append("</table>" +
			           "</body>" +
			           "</html>");
			String html = buf.toString();
			writer.println(html);
			String query4 = "select sum(cost) from temproute";
			ResultSet tot = stat.executeQuery(query4);
			tot.next();
			String sum = tot.getString(1);
			double sum1 = Double.parseDouble(sum);
			writer.println("<br/>");
			writer.println("<br/>");
			writer.println("Total cost is "+ sum1);
			if(val==1)
			{
				PreparedStatement ps = conn.prepareStatement("insert into history values(?,?,?,?,?)");
				ps.setString(1, sess_name);
				ps.setDouble(2, fromx);
				ps.setDouble(3, fromy);
				ps.setDouble(4, destx);
				ps.setDouble(5, desty);
				int k =ps.executeUpdate();
				if(k>0) {
				System.out.println("successful");
				}
				else
					System.out.println("Unsccessful");
			}
		rs.close();
		
		String blockQuery1 = "update texas_roads_gcs set length=length*1000 where gid=(select gid from (select gid,length,ST_DWithin(ST_GeomFromText(POINT("+x+" "+y+")),geom,0.05) from texas_roads_gcs where ST_DWithin(ST_GeomFromText(POINT("+x+" "+y+")),geom,0.05)='t')as s)";
		stat.execute(blockQuery1);
		stat.close();
		conn.close();
	}
	catch(SQLException e){
		System.out.println("exception"+e);

	}catch(ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}		
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
