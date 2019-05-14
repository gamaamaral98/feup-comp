class Fac {
    public boolean ComputeFac(int num){
        boolean a;
        boolean b;

        a=true;
        b=false;
        if (num < 1)
            a = false;
        else
            b = true;

        return a && b;
    }

    public static void main(String[] a){
        new Fac().ComputeFac(10);
    }
}