.class public Fac
.super java/lang/Object


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
	iconst_0
	invokevirtual Fac/ComputeFac(I)I
	invokestatic io/println(I)V
	return

.end method

.method public ComputeFac(I)I

	.limit locals 2
	.limit stack 999

	iload 1
	invokestatic io/println(I)V
	iload 1
	ireturn

.end method
