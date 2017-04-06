package Urltest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class Http {
	
	
	public static int CheckFileRepeat(File file){
		if(file.exists()){
			return 1;
		}else {
			return 0;
		}
	}
	
	public  void DownLoad(String durl) {
		String fpath = "D:/Java/javaTest/";
		File file;
		BufferedOutputStream bufout;
		try{
			
            URL u = new URL(durl);
            String path = null;
            
            //打印输入信息
            
//			获得URL连接
			URLConnection conn = u.openConnection();
			conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			
//			获取URL的类型，并剔除，只剩下坐后缀名的字符串
			String type = conn.getContentType();
			String ima = type;
			type = type.split("; ")[0].split("/")[1];
			
//			如果不能通过链接获取名字，就设置名字为域名
			String name = u.getFile();
			if(name==""){
				name = u.getHost();
			}else{
				int len = name.split("/").length;
				name = name.split("/")[len-1].split("\\.")[0];
			}
			
			
//			创建本地文件
			file = new File(fpath + name + "." + type);
			int FileNameCount = 0;
//			检查是否有重名文件
			while(CheckFileRepeat(file)==1){
				file = new File(fpath + name + "(" +FileNameCount + ")" + "." + type);
				FileNameCount ++;
			}
			
//			开启流输入本地
			OutputStream os = new FileOutputStream(file);
			bufout = new BufferedOutputStream(os);
			
            
            Socket socket= new Socket(u.getHost(),80);   
            BufferedWriter out= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            path = u.getPath();
            if(path.equals(""))
            	path = "/";
            //发送HTTP请求
            String s = "GET " + path + " HTTP/1.1\r\n";
            s += "Host: " + u.getHost() + "\r\n";
            s += "user-agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)\r\n";
            s += "Connection: keep-alive\r\n\r\n";
            System.out.println(s);
            
            out.write(s);
            out.flush();
            
            byte[] rb = new byte[1024];
            boolean eohFound = false;
            int len, offset;
            while ((len = in.read(rb)) != -1) {
            	offset = 0;
            	if(!eohFound){
                    String string = new String(rb, 0, len);
                    int indexOfEOH = string.indexOf("\r\n\r\n");
                    if(indexOfEOH != -1) {
                    	len = len-indexOfEOH-4;
                    	offset = indexOfEOH+4;
                        eohFound = true;
                    } else {
                    	len = 0;
                    }
                }
            	bufout.write(rb, offset, len);
            	bufout.flush();
			}
            socket.close();
            os.close();
        }
        catch (MalformedURLException ex) {
            System.err.println("This is not a parseable URL");
        }
        catch(IOException e){
             System.err.println(e);
        }
	}
	public static void main(String []args) {
		Http http = new Http();
		http.DownLoad("http://cutestar.cc/images/chrome-logo.jpg");
	}
}
