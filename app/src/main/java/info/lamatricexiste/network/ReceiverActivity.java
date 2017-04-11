package info.lamatricexiste.network;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Hashtable;

import android.app.Activity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;
public class ReceiverActivity extends Activity {

    TextView infoIp, infoPort;

    static final int SocketServerPORT = 8080;
    ServerSocket serverSocket;

    ServerSocketThread serverSocketThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        infoIp = (TextView) findViewById(R.id.infoip);
        infoPort = (TextView) findViewById(R.id.infoport);

        infoIp.setText(getIpAddress());

        serverSocketThread = new ServerSocketThread();
        serverSocketThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    public class ServerSocketThread extends Thread {

        @Override
        public void run() {
            Socket socket = null;

            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                ReceiverActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        infoPort.setText("I'm waiting here: "
                                + serverSocket.getLocalPort());
                    }});

                while (true) {
                    socket = serverSocket.accept();
                    FileTxThread fileTxThread = new FileTxThread(socket);
                    fileTxThread.start();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public class FileTxThread extends Thread {
        Socket socket;

        FileTxThread(Socket socket){
            this.socket= socket;
        }

        @Override
        public void run() {
            Socket sock = null;
            try {
                sock = new Socket("127.0.0.1", SocketServerPORT);
                ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                Hashtable hash = (Hashtable) ois.readObject();
                Toast.makeText(ReceiverActivity.this,"Hash:"+hash,Toast.LENGTH_LONG).show();
                System.out.println(hash);
                ois.close();
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


            File file = new File(
                    Environment.getExternalStorageDirectory(),
                    "test.txt");

            byte[] bytes = new byte[(int) file.length()];
            BufferedInputStream bis;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytes, 0, bytes.length);
                OutputStream os = socket.getOutputStream();
                os.write(bytes, 0, bytes.length);
                os.flush();
                socket.close();

                final String sentMsg = "File sent to: " + socket.getInetAddress();
                ReceiverActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(ReceiverActivity.this,
                                sentMsg,
                                Toast.LENGTH_LONG).show();
                    }});

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }
}
