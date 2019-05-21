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
	.limit stack 999

	iload 2
	ireturn

.end method

.method public static main([Ljava/lang/String;)V

	.limit locals 9
	.limit stack 999

	new Fac
	dup
	invokenonvirtual Fac/<init>()V
	invokestatic ioPlus/println(Fac)V
	return

.end method
