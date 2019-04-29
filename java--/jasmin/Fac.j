.class public Fac
.super java/lang/Object


.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method


; methods

.method public ComputeFac(I;)I

	.limit locals 3

	iload 2
	ireturn

.end method

.method public static main([Ljava/lang/String;)V

	.limit locals 1

	new Fac
	dup
	invokenonvirtual Fac<init>()V
	bipush 10
	invokevirtual Fac/ComputeFac(I;)I
	invokestatic io/println(I;)V
	pop

.end method
