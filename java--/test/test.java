class Fac {
	public int ComputeFac(int num){
		int num_aux;
		if (num < 1) {
			io.println(num);
			num_aux = 1;
		}
		else {
			io.println(num);
			num_aux = num * (this.ComputeFac(num-1));
		}
		return num_aux;
	}
	public static void main(String[] a){
		io.println(new Fac().ComputeFac(10));
	}
 }
