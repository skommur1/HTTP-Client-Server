import java.net.ServerSocket;
import java.io.*;
import java.net.Socket;

class Server implements Runnable{
	protected int          serverPort;
    protected ServerSocket serverSocket;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;

    public Server(int port){
        this.serverPort = port;
    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        }
        while(! isStopped ){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            new Thread(new WorkerRunnable(clientSocket)).start();
            
        }
    }
    public synchronized void stop(){
        this.isStopped = true;
        try {
        	this.serverSocket.close();
        } catch (Exception e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
}

class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    public final static int FILE_SIZE = 6022386;
    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
    	OutputStream output;
    	String statusLine;
    	String typeLine, length;
    	InputStream input;
    	PrintWriter pw;
        try {
            input  = clientSocket.getInputStream();
            output = clientSocket.getOutputStream();
            pw = new PrintWriter(output,true);
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            String header = in.readLine();
            String arr[] = header.split(" ");
            
            String command = arr[0];
            
            String filename=arr[1];
            
            	if(command.equals("GET"))
            	{
            		File f = new File(filename);
            		if(f.exists())
                    {
            		FileReader fr = new FileReader(filename);
            		BufferedReader br = new BufferedReader(fr);
            		statusLine = "HTTP/1.1 200 OK" + "\r\n";
            		typeLine = "Content-type: html/text  \r\n";
            		length = "Content-Length: "+f.length()+"\r\n";
            		String ch;
            		pw.print(statusLine);
            		pw.print(typeLine);
            		pw.print(length);
            		while((ch = br.readLine())!=null)
            		{
            			pw.println(ch);
					
            		}
            		fr.close();
            		br.close();
                    }
            		else
                    {
                    	statusLine = "HTTP/1.1 404 Not Found" + "\r\n";
                    	pw.println(statusLine);
                    }
            	}
            	else if(command.equals("PUT"))
            	{
            		String username = System.getProperty("user.name");
            		String filename1 = "C:\\Users\\"+username+"\\Documents\\"+filename;
            		File f1 =  new File(filename1);
            		FileWriter fw = new FileWriter(f1,false);
            		BufferedWriter bw = new BufferedWriter(fw);
            		statusLine = "HTTP/1.1 200 OK File Created" + "\r\n";
            		String ch;
            		pw.println(statusLine);
            		while ((ch=in.readLine())!=null)
            		{
            			bw.write(ch);
            			bw.newLine();
            		}
            		bw.close();
            	}
            
        	pw.close();
        	output.close();
            input.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}

class Multi
{
	public static void main(String args[])
	{
		if(args.length!=1)
		{
			System.out.println("Invalid number of arguments");
			System.exit(0);
		}
		Server server=new Server(Integer.parseInt(args[0]));
		new Thread(server).start();
		System.out.println("Server running...");
	}
}