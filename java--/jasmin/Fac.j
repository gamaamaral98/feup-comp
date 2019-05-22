.class public Fac
.super java/lang/Object

.field a I

.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method


; methods

.method public ComputeFac(IFac;)Fac

	.limit locals 4
	.limit stack 999

	ldc 123456
	invokestatic io/println(I)Z
	ifne label_0
	ldc 123456
	invokestatic io/println(I)V
	iconst_1
	istore 3

	goto label_1
	label_0:
	iload 1
	aload_0
	iload 1
	iconst_1
	isub
	invokevirtual Fac/ComputeFac(IFac;)Fac
	imul
	istore 3

	label_1:
	ldc 123456
	invokestatic io/println(I)Fac
	ireturn

.end method

.method public static main([Ljava/lang/String;)V

	.limit locals 2
	.limit stack 999

	new Fac
	dup
	invokespecial Fac/<init>()V
	bipush 10
	invokevirtual Fac/ComputeFac(IFac;)Fac
	invokestatic io/println(Fac;)V
	return

.end method
