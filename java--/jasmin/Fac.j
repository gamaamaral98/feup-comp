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

.method public ComputeFac(I;)I

<<<<<<< HEAD
	.limit locals 2
=======
	.limit locals 3
>>>>>>> 9211d199402c3ccdfe53621d4ee70b16b3d518f5

	iload 2
	ireturn

.end method

.method public static main([Ljava/lang/String;)V

	.limit locals 1

<<<<<<< HEAD
	bipush 10
	invokevirtual java/lang/Fac/ComputeFac(I;)I
	invokestatic io.println(I;)V
=======
	new Fac
	dup
	invokenonvirtual Fac<init>()V
	bipush 10
	invokevirtual Fac/ComputeFac(I;)I
	invokestatic io/println(I;)V
>>>>>>> 9211d199402c3ccdfe53621d4ee70b16b3d518f5
	pop

.end method
>>>>>>> b60074a0094209230cfd6047a177516aa9dfd522
