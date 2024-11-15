import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class SimpleFTPSUpload {
    public static void main(String[] args) {
        String server = "ftps.example.com";
        int port = 990;  // Обычно FTPS использует порт 990
        String user = "your_username";
        String password = "your_password";
        String localFilePath = "C:/path/to/your/local/file.txt";
        String remoteFilePath = "/path/on/server/file.txt";

        SSLSocket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;

        try {
            // Создаем SSL-сокет для подключения к серверу FTPS
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(server, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Логинимся на сервере
            System.out.println("Ответ сервера: " + reader.readLine());
            writer.println("USER " + user);
            System.out.println("Ответ сервера: " + reader.readLine());
            writer.println("PASS " + password);
            System.out.println("Ответ сервера: " + reader.readLine());

            // Устанавливаем тип передачи в бинарный
            writer.println("TYPE I");
            System.out.println("Ответ сервера: " + reader.readLine());

            // Переходим в режим пассивной передачи
            writer.println("PASV");
            String response = reader.readLine();
            System.out.println("Ответ сервера: " + response);

            // Парсим адрес и порт для передачи данных
            String[] parts = response.split("[()]")[1].split(",");
            String dataHost = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
            int dataPort = (Integer.parseInt(parts[4]) << 8) + Integer.parseInt(parts[5]);

            // Открываем подключение для передачи данных
            SSLSocket dataSocket = (SSLSocket) factory.createSocket(dataHost, dataPort);
            OutputStream dataOut = dataSocket.getOutputStream();

            // Подготавливаем файл к передаче
            writer.println("STOR " + remoteFilePath);
            System.out.println("Ответ сервера: " + reader.readLine());

            // Передаем файл
            try (FileInputStream fileInput = new FileInputStream(localFilePath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInput.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, bytesRead);
                }
                dataOut.flush();
                System.out.println("Файл успешно загружен.");
            }

            dataSocket.close();
            System.out.println("Ответ сервера: " + reader.readLine());

            // Завершаем сессию
            writer.println("QUIT");
            System.out.println("Ответ сервера: " + reader.readLine());

        } catch (IOException ex) {
            System.out.println("Ошибка: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (writer != null) writer.close();
                if (reader != null) reader.close();
                if (socket != null) socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}