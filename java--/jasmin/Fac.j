.class public Fac
.super java/lang/Object

.field test_arr1 [I

.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method


; methods

.method public static main([Ljava/lang/String;)V

	.limit locals 2
	.limit stack 999

	new Fac
	dup
	invokespecial Fac/<init>()V
	bipush 10
	invokevirtual Fac/ComputeFac(I)I
	invokestatic io/println(I)V
	return

.end method

.method public bla(I)I

	.limit locals 2
	.limit stack 999

	iload 1
	ireturn

.end method

.method public ComputeFac(I)I

	.limit locals 3
	.limit stack 999

	iconst_2
	newarray int
	astore 2

	aload 2
	iconst_0
	bipush 10
	iastore 

	aload 2
	iconst_0
	iaload
	ireturn

.end method
