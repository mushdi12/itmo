import  static
        java.lang.Math. *;
public class Intro {

    public static void 	main(String[] args) {

        int [] [] mr = {{1,2} ,{2,3}};

        for (int i = 0; i < 2; i++) {
            for (int j = 0 ; j < 2; j++) {
                System.out.print(mr[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }

        int []  mr1 = {1,2,2,3};

        for (int i = 0; i < 4; i++) {
            System.out.print(mr1[i]);
            System.out.print(" ");
        }
        System.out.println();
    }   
	   


}
