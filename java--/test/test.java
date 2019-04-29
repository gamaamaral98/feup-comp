class Fac {
    public boolean ComputeFac(int num){
        boolean a;
        boolean b;

        a=true;
        b=false;
        // if (num < 1)
        //     num_aux = 1;
        // else
        //     num_aux = num * (this.ComputeFac(num-1));

        return a && b;
    }

    public static void main(String[] a){
        io.println(new Fac().ComputeFac(10));
    }
}