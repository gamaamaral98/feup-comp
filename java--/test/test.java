class Fac {
	int a;

	public Fac ComputeFac(int num, Fac a){
		int num_aux;
		if (!io.println(123456)) {
			io.println(123456);
			num_aux = 1;
		}
		else {
			num_aux = num * (this.ComputeFac(num-1));
		}
		return io.println(123456);
	}
	public static void main(String[] a){
		io.println(new Fac().ComputeFac(10));
	}
 }
