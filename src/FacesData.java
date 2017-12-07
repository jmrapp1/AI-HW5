import java.io.File;

public class FacesData {

    private int[] subjects;
    private int[][] faces;

    public FacesData(int[] subjects, int[][] faces) {
        this.subjects = subjects;
        this.faces = faces;
    }

    public int[] getSubjects() {
        return subjects;
    }

    public int[][] getFaces() {
        return faces;
    }
}
