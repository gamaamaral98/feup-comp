.class public Fac
.super java/lang/Object


.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method


; methods

.method public ComputeFac(I)I

	.limit locals 3
	.limit stack 999

	iload 1
	invokestatic io/println(I)V
	iload 1
	iconst_1
	if_icmpge label_0
	ldc 123456
	invokestatic io/println(I)V
	iconst_1
	istore 2

	goto label_1
	label_0:
	iload 1
	aload_0
	iload 1
	iconst_1
	isub
	invokevirtual Fac/ComputeFac(I)I
	imul
	istore 2

	label_1:
	iload 2
	ireturn

.end method

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
