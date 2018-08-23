package utilstest;

import com.utils.OperatingSystem;
import com.utils.TempDirCreator;
import com.utils.ffmpeghandler.ChunkGenerater;
import com.utils.ffmpeghandler.CommandType;
import com.utils.ffmpeghandler.FFmpegListener;
import com.utils.filehandler.FilesHandler;

import java.io.IOException;
import java.util.UUID;

public class ChunkGeneraterTest implements FFmpegListener {
    private String tempDir = null;
    private String chunkFile;
    private String input;

    public ChunkGeneraterTest() throws IOException {
        OperatingSystem operatingSystem = OperatingSystem.detectOperatingSystem();
        String uniqueId = UUID.randomUUID().toString();

        try {
            tempDir = TempDirCreator.initializeTemporaryDirectory(uniqueId, operatingSystem);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert (tempDir != null);

        try {
            FilesHandler.LocalCopy("./res/test_files/big_buck_bunny.mp4", tempDir + "big_buck_bunny.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }

        input = tempDir + "big_buck_bunny.mp4";
        ChunkGenerater.GenerateChunks(tempDir + "ffmpeg", input, this);
    }

    @Override
    public void onEncoderStart(String filename) {
        System.out.println("Started generating chunks for: " + filename);
    }

    @Override
    public void onProgressUpdate(double progress) {
        System.out.println("Progress: " + progress + "%");
    }

    @Override
    public void onJobDone(CommandType type) {
        System.out.println("Done generating chunks!");
        try {
            chunkFile = ChunkGenerater.GenerateConcatFile(tempDir);
            FilesHandler.DoSingleFileCleanUp(input);
            new ChunkConcatenaterTest(tempDir + "ffmpeg", chunkFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new ChunkGeneraterTest();
    }
}
