import  static
        java.lang.Math. *;
public class Lab0 {
   public static void main(String[] args){
	    // 1 задание 
        long[] ms1 = new long[21 - 3];
        for(int i = 3; i <21; i++){
			ms1[i-3] = i;
			}
		
		System.out.println();
		// 2 задание 
		float [] ms2 = new float[17];
        for(int i = 0; i <17; i++){
			ms2[i] = ((float)(Math.random() * 15) - 6);
			}
			
		System.out.println();
		
		double[][] ms3 = new double[18][17];
		for (int i = 0; i < 18;i++){
			for(int j = 0; j < 17;j++){
				float x = ms2[j];
				if (ms1[i] == 20) {
					ms3[i][j] = Math.exp(Math.pow((0.25 / Math.pow(1 - (3/4 * (x+1)),x)),Math.tan(x)));
				}
				if (((ms1[i] == 3) || (ms1[i] == 5)) || ((ms1[i] == 6) || (ms1[i] == 8)) 
					|| ((ms1[i] == 14) || (ms1[i] == 15)) || ((ms1[i] == 16) || (ms1[i] == 17)) || (ms1[i] == 18))  {
					ms3[i][j] = Math.tan(Math.exp((2*(Math.PI/(Math.PI/2 + Math.abs(x)))) * (2*(Math.PI/(Math.PI/2 + Math.abs(x))))));
				}else{
					
				ms3[i][j] = Math.pow(( Math.pow((4/Math.exp(x)),Math.cos(x)*(Math.sin(Math.exp(Math.abs(x)) * (Math.atan(Math.cos(x)) + 0.5))))/2),Math.cos(Math.sin(Math.pow((x/2),3))));
				}
				
									 
			}
		}		
		// 3 задание
		for (int i = 0; i <18;i++ ){
			for (int j = 0 ; j  < 17; j++ ){
				System.out.printf("%.2f",ms3[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
						
					
   }		 
} 