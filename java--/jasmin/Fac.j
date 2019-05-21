.class public Fac
.super java/lang/Object

.field a I

.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method


; methods

.method public ComputeFac(I)I

	.limit locals 9
	.limit stack 999

	aload_0
	bipush 14
	putfield Fac/a I

	aload_0
	getfield Fac/a I
	ireturn

.end method

.method public static main([Ljava/lang/String;)V

	.limit locals 9
	.limit stack 999

	new Fac
	dup
	invokespecial Fac/<init>()V
	bipush 10
	invokevirtual Fac/ComputeFac(I)I
	invokestatic io/println(I)V
	return

.end method
