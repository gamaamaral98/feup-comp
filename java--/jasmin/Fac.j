.class public Fac
.super java/lang/Object


.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method


; methods

.method public ComputeFac(I)Z

	.limit locals 9
	.limit stack 9

	iconst_1
	istore 2

	iconst_0
	istore 3

	iconst_0
	istore 2

	iconst_1
	istore 3

	iload 2
	ifeq label66
	iload 3
	ifeq label66
	iconst_1
	goto label67
	label66:
	iconst_0
	label67:
	ireturn

.end method

.method public static main([Ljava/lang/String;)V

	.limit locals 9
	.limit stack 9

	new Fac
	dup
	invokenonvirtual Fac/<init>()V
	bipush 10
	invokevirtual Fac/ComputeFac(I)Z
	return

.end method
