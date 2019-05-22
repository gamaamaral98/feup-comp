class Fac {

	public static void main(String[] a){
		io.println(new Fac().ComputeFac(0));
	}

	public int ComputeFac(int num){

		io.println(num);

		while(num < 5){

			io.println(num);
			num = num + 1;
		}

		return num;
	}
}