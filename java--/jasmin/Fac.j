<<<<<<< HEAD
=======
.class public Fac
.super java/lang/Object


.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method


; methods

.method public ComputeFac(I)I

	.limit locals 9

	.limit stack 9

	iconst_1
	iconst_2
	iadd
	ireturn

.end method

.method public static main([Ljava/lang/String;)V

	.limit locals 9

	.limit stack 9

	new Fac
	dup
	invokenonvirtual Fac/<init>()V
	bipush 10
	invokevirtual Fac/ComputeFac(I)I
	invokestatic io/println(I)V
	return

.end method
>>>>>>> b60074a0094209230cfd6047a177516aa9dfd522
