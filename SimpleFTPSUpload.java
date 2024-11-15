import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

public class FTPESUploaderWithSessionReuse {
    public static void main(String[] args) {
        String server = "ftps.example.com";
        int port = 21;
        String user = "your_username";
        String password = "your_password";
        String localFilePath = "C:/path/to/your/local/file.txt";
        String remoteFilePath = "/path/on/server/file.txt";

        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            // Подключение по FTP
            socket = new Socket(server, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Ответ сервера: " + reader.readLine());

            // Переход в режим AUTH TLS
            writer.println("AUTH TLS");
            System.out.println("Ответ сервера: " + reader.readLine());

            // Устанавливаем SSL-соединение
            SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) sslFactory.createSocket(socket, server, port, true);
            sslSocket.startHandshake();

            // Сохраняем SSLSession для повторного использования
            final SSLContext sslContext = SSLContext.getDefault();
            final SSLSession controlSession = sslSocket.getSession();

            reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            writer = new PrintWriter(sslSocket.getOutputStream(), true);

            // Авторизация
            writer.println("USER " + user);
            System.out.println("Ответ сервера: " + reader.readLine());
            writer.println("PASS " + password);
            System.out.println("Ответ сервера: " + reader.readLine());

            // Включаем шифрование для data connection
            writer.println("PROT P");
            System.out.println("Ответ сервера: " + reader.readLine());

            // Устанавливаем бинарный режим
            writer.println("TYPE I");
            System.out.println("Ответ сервера: " + reader.readLine());

            // Переход в пассивный режим
            writer.println("PASV");
            String response = reader.readLine();
            System.out.println("Ответ сервера: " + response);

            // Парсим адрес и порт для data connection
            String[] parts = response.split("[()]")[1].split(",");
            String dataHost = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
            int dataPort = (Integer.parseInt(parts[4]) << 8) + Integer.parseInt(parts[5]);

            // Создаем защищённое соединение для передачи данных
            SSLSocket dataSocket = (SSLSocket) sslFactory.createSocket(dataHost, dataPort);
            dataSocket.setEnableSessionCreation(false); // Отключаем создание новой сессии
            dataSocket.startHandshake();

            // Повторно используем SSL-сессию
            dataSocket.setSSLParameters(sslContext.getDefaultSSLParameters());
            dataSocket.getSession().invalidate();
            dataSocket.startHandshake();

            // Передача файла
            writer.println("STOR " + remoteFilePath);
            System.out.println("Ответ сервера: " + reader.readLine());

            try (OutputStream dataOut = dataSocket.getOutputStream();
                 FileInputStream fileInput = new FileInputStream(localFilePath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInput.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, bytesRead);
                }
                dataOut.flush();
            }

            System.out.println("Ответ сервера: " + reader.readLine());

            // Закрываем соединения
            dataSocket.close();
            writer.println("QUIT");
            System.out.println("Ответ сервера: " + reader.readLine());

        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
