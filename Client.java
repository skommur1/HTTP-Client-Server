import java.io.*;
import java.net.Socket;
import java.net.InetAddress;

public class Client {
	
	public static void main(String args[])
	{
		if(args.length!=4)
		{
			System.out.println("Invalid number of arguments");
			System.exit(0);
		}
		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		String command = args[2];
		String filename = args[3];
		try
		{
			Socket clientSocket = new Socket(InetAddress.getByName(hostname), port);
			OutputStream out = clientSocket.getOutputStream();
			PrintWriter pw = new PrintWriter(out,true);
			InputStream in = clientSocket.getInputStream();
	        InputStreamReader isr = new InputStreamReader(in);
			if (command.equalsIgnoreCase("GET"))
			{
				pw.println("GET "+filename+" HTTP/1.1\r");
				pw.println("Accept: text/plain, text/html, text/*\r");

		        BufferedReader br = new BufferedReader(isr);
		        String line;
	            while( (line = br.readLine())!= null )
	            {
	                System.out.println( line );

	            }
	            br.close();
			}
			else if(command.equalsIgnoreCase("PUT"))
			{
				
				File f = new File(filename);
				if(f.exists())
				{
					pw.println("PUT "+filename+" HTTP/1.1\r");
					FileReader fr = new FileReader(f);
					BufferedReader bwr = new BufferedReader(fr);
					String ch;
					while((ch = bwr.readLine())!=null)
					{
						pw.println(ch);
					}
					BufferedReader br = new BufferedReader(isr);
					String response;
					response = br.readLine();
					System.out.println(response);
					fr.close();
					bwr.close();
				}
				else
				{
					System.err.println("File not found");
				}
				pw.close();
				out.close();
			}
			
			isr.close();
			clientSocket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	

}
