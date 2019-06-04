class Test {
    int[] a;
    public static void main(String[] args) {
        new Test().a();
    }

    public int a() {
        a = new int[5];
        io.println(a.length);
        return 1;
    }
}
