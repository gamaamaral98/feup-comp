class Fac {

	int[] test_arr1;
	int sup;

	public static void main(String[] a){
		io.println(new Fac().ComputeFac(10));
	}

	public int ComputeFac(int num){

		int[] test_arr2;

		sup = 0;

		test_arr1 = new int[5];				// done
		test_arr2 = new int[5];				// done

		test_arr1[0] = num;					// 
		test_arr2[4] = num;					// 

		// test_arr1[0] = sup;				// 
		// test_arr2[4] = sup;				// 

		// num = test_arr1[0];					// done
		// num = test_arr2[4];					// done

		// sup = test_arr1[0];				// done
		// sup = test_arr2[4];				// done

		return 0;
	}
}