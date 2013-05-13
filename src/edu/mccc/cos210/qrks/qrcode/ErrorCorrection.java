package edu.mccc.cos210.qrks.qrcode;
import java.util.*;

public class ErrorCorrection {
	public static byte[][] generatorPolynomials = {
		{1, (byte)25, (byte)0},  /*2*/
		{10, (byte)119, (byte)166, (byte)164, (byte)113, (byte)0},  /*5*/
		{15, (byte)176, (byte)5, (byte)134, (byte)0, (byte)116, (byte)0},  /*6*/
		{21, (byte)102, (byte)238, (byte)149, (byte)146, (byte)229, (byte)87, (byte)0}, /*7*/
		{28, (byte)196, (byte)252, (byte)215, (byte)249, (byte)208, (byte)238, (byte)175, (byte)0},  /*8*/
		{45, (byte)32, (byte)94, (byte)64, (byte)70, (byte)118, (byte)61, (byte)46, (byte)67, (byte)251, (byte)0}, /*10*/
		{78, (byte)140, (byte)206, (byte)218, (byte)130, (byte)104, (byte)106, (byte)100, (byte)86, (byte)100, (byte)176, (byte)152, (byte)74, (byte)0}, /*13*/
		{91, (byte)22, (byte)59, (byte)207, (byte)87, (byte)216, (byte)137, (byte)218, (byte)124, (byte)190, (byte)48, (byte)155, (byte)249, (byte)199, (byte)0},  /*14*/
		{105, (byte)99, (byte)5, (byte)124, (byte)140, (byte)237, (byte)58, (byte)58, (byte)51, (byte)37, (byte)202, (byte)91, (byte)61, (byte)183, (byte)8, (byte)0},  /*15*/
		{120, (byte)225, (byte)194, (byte)182, (byte)169, (byte)147, (byte)191, (byte)91, (byte)3, (byte)76, (byte)161, (byte)102, (byte)109, (byte)107, (byte)104, (byte)120, (byte)0}, /*16*/
		{(byte)136, (byte)163, (byte)243, (byte)39, (byte)150, (byte)99, (byte)24, (byte)147, (byte)214, (byte)206, (byte)123, (byte)239, (byte)43, (byte)78, (byte)206, (byte)139, (byte)43, (byte)0},  /*17*/
		{(byte)153, (byte)96, (byte)98, (byte)5, (byte)179, (byte)252, (byte)148, (byte)152, (byte)187, (byte)79, (byte)170, (byte)118, (byte)97, (byte)184, (byte)94, (byte)158, (byte)234, (byte)215, (byte)0},  /*18*/
		{(byte)190, (byte)188, (byte)212, (byte)212, (byte)164, (byte)156, (byte)239, (byte)83, (byte)225, (byte)221, (byte)180, (byte)202, (byte)187, (byte)26, (byte)163, (byte)61, (byte)50, (byte)79, (byte)60, (byte)17, (byte)0},  /*20*/
		{(byte)231, (byte)165, (byte)105, (byte)160, (byte)134, (byte)219, (byte)80, (byte)98, (byte)172, (byte)8, (byte)74, (byte)200, (byte)53, (byte)221, (byte)109, (byte)14, (byte)230, (byte)93, (byte)242, (byte)247, (byte)171, (byte)210, (byte)0}, /*22*/
		{21, (byte)227, (byte)96, (byte)87, (byte)232, (byte)117, (byte)0, (byte)111, (byte)218, (byte)228, (byte)226, (byte)192, (byte)152, (byte)169, (byte)180, (byte)159, (byte)126, (byte)251, (byte)117, (byte)211, (byte)48, (byte)135, (byte)121, (byte)229, (byte)0},  /*24*/
		{70, (byte)218, (byte)145, (byte)153, (byte)227, (byte)48, (byte)102, (byte)13, (byte)142, (byte)245, (byte)21, (byte)161, (byte)53, (byte)165, (byte)28, (byte)111, (byte)201, (byte)145, (byte)17, (byte)118, (byte)182, (byte)103, (byte)2, (byte)158, (byte)125, (byte)173, (byte)0},  /*26*/
		{123, (byte)9, (byte)37, (byte)242, (byte)119, (byte)212, (byte)195, (byte)42, (byte)87, (byte)245, (byte)43, (byte)21, (byte)201, (byte)232, (byte)27, (byte)205, (byte)147, (byte)195, (byte)190, (byte)110, (byte)180, (byte)108, (byte)234, (byte)224, (byte)104, (byte)200, (byte)223, (byte)168, (byte)0},  /*28*/
		{(byte)180, (byte)192, (byte)40, (byte)238, (byte)216, (byte)251, (byte)37, (byte)156, (byte)130, (byte)224, (byte)193, (byte)226, (byte)173, (byte)42, (byte)125, (byte)222, (byte)96, (byte)239, (byte)86, (byte)110, (byte)48, (byte)50, (byte)182, (byte)179, (byte)31, (byte)216, (byte)152, (byte)145, (byte)173, (byte)41, (byte)0},  /*30*/
		{(byte)241, (byte)220, (byte)185, (byte)254, (byte)52, (byte)80, (byte)222, (byte)28, (byte)60, (byte)171, (byte)69, (byte)38, (byte)156, (byte)80, (byte)185, (byte)120, (byte)27, (byte)89, (byte)123, (byte)242, (byte)32, (byte)138, (byte)138, (byte)209, (byte)67, (byte)4, (byte)167, (byte)249, (byte)190, (byte)106, (byte)6, (byte)10, (byte)0},  /*32*/
		{51, (byte)129, (byte)62, (byte)98, (byte)13, (byte)167, (byte)129, (byte)183, (byte)61, (byte)114, (byte)70, (byte)56, (byte)103, (byte)218, (byte)239, (byte)229, (byte)158, (byte)58, (byte)125, (byte)163, (byte)140, (byte)86, (byte)193, (byte)113, (byte)94, (byte)105, (byte)19, (byte)108, (byte)21, (byte)26, (byte)94, (byte)146, (byte)77, (byte)111, (byte)0}, /*34*/
		{120, (byte)30, (byte)233, (byte)113, (byte)251, (byte)117, (byte)196, (byte)121, (byte)74, (byte)120, (byte)177, (byte)105, (byte)210, (byte)87, (byte)37, (byte)218, (byte)63, (byte)18, (byte)107, (byte)238, (byte)248, (byte)113, (byte)152, (byte)167, (byte)0, (byte)115, (byte)152, (byte)60, (byte)234, (byte)246, (byte)31, (byte)172, (byte)16, (byte)98, (byte)183, (byte)200, (byte)0},  /*36*/
		{15, (byte)35, (byte)53, (byte)232, (byte)20, (byte)72, (byte)134, (byte)125, (byte)163, (byte)47, (byte)41, (byte)88, (byte)114, (byte)181, (byte)35, (byte)175, (byte)7, (byte)170, (byte)104, (byte)226, (byte)174, (byte)187, (byte)26, (byte)53, (byte)106, (byte)235, (byte)56, (byte)163, (byte)57, (byte)247, (byte)161, (byte)128, (byte)205, (byte)128, (byte)98, (byte)252, (byte)161, (byte)79, (byte)116, (byte)59, (byte)0}, /*40*/
		{96, (byte)50, (byte)117, (byte)194, (byte)162, (byte)171, (byte)123, (byte)201, (byte)254, (byte)237, (byte)199, (byte)213, (byte)101, (byte)39, (byte)223, (byte)101, (byte)34, (byte)139, (byte)131, (byte)15, (byte)147, (byte)96, (byte)106, (byte)188, (byte)8, (byte)230, (byte)84, (byte)110, (byte)191, (byte)221, (byte)242, (byte)58, (byte)3, (byte)0, (byte)231, (byte)137, (byte)18, (byte)25, (byte)230, (byte)221, (byte)103, (byte)250, (byte)0},  /*42*/
		{(byte)181, (byte)73, (byte)102, (byte)113, (byte)130, (byte)37, (byte)169, (byte)204, (byte)147, (byte)217, (byte)194, (byte)52, (byte)163, (byte)68, (byte)114, (byte)118, (byte)126, (byte)224, (byte)62, (byte)143, (byte)78, (byte)44, (byte)238, (byte)1, (byte)247, (byte)14, (byte)145, (byte)9, (byte)123, (byte)72, (byte)25, (byte)191, (byte)243, (byte)89, (byte)188, (byte)168, (byte)55, (byte)69, (byte)246, (byte)71, (byte)121, (byte)61, (byte)7, (byte)190, (byte)0},  /*44*/
		{15, (byte)82, (byte)19, (byte)223, (byte)202, (byte)43, (byte)224, (byte)157, (byte)25, (byte)52, (byte)174, (byte)119, (byte)245, (byte)249, (byte)8, (byte)234, (byte)104, (byte)73, (byte)241, (byte)60, (byte)96, (byte)4, (byte)1, (byte)36, (byte)211, (byte)169, (byte)216, (byte)135, (byte)16, (byte)58, (byte)44, (byte)129, (byte)113, (byte)54, (byte)5, (byte)89, (byte)99, (byte)187, (byte)115, (byte)202, (byte)224, (byte)253, (byte)112, (byte)88, (byte)94, (byte)112, (byte)0},  /*46*/
		{108, (byte)34, (byte)39, (byte)163, (byte)50, (byte)84, (byte)227, (byte)94, (byte)11, (byte)191, (byte)238, (byte)140, (byte)156, (byte)247, (byte)21, (byte)91, (byte)184, (byte)120, (byte)150, (byte)95, (byte)206, (byte)107, (byte)205, (byte)182, (byte)160, (byte)135, (byte)111, (byte)221, (byte)18, (byte)115, (byte)123, (byte)46, (byte)63, (byte)178, (byte)61, (byte)240, (byte)102, (byte)39, (byte)90, (byte)251, (byte)24, (byte)60, (byte)146, (byte)211, (byte)130, (byte)196, (byte)25, (byte)228, (byte)0},  /*48*/
		{(byte)205, (byte)133, (byte)232, (byte)215, (byte)170, (byte)124, (byte)175, (byte)235, (byte)114, (byte)228, (byte)69, (byte)124, (byte)65, (byte)113, (byte)32, (byte)189, (byte)42, (byte)77, (byte)75, (byte)242, (byte)215, (byte)242, (byte)160, (byte)130, (byte)209, (byte)126, (byte)160, (byte)32, (byte)13, (byte)46, (byte)225, (byte)203, (byte)242, (byte)195, (byte)111, (byte)209, (byte)3, (byte)35, (byte)193, (byte)203, (byte)99, (byte)209, (byte)46, (byte)118, (byte)9, (byte)164, (byte)161, (byte)157, (byte)125, (byte)232, (byte)0}, /*50*/
		{51, (byte)116, (byte)254, (byte)239, (byte)33, (byte)101, (byte)220, (byte)200, (byte)242, (byte)39, (byte)97, (byte)86, (byte)76, (byte)22, (byte)121, (byte)235, (byte)233, (byte)100, (byte)113, (byte)124, (byte)65, (byte)59, (byte)94, (byte)190, (byte)89, (byte)254, (byte)134, (byte)203, (byte)242, (byte)37, (byte)145, (byte)59, (byte)14, (byte)22, (byte)215, (byte)151, (byte)233, (byte)184, (byte)19, (byte)124, (byte)127, (byte)86, (byte)46, (byte)192, (byte)89, (byte)251, (byte)220, (byte)50, (byte)186, (byte)86, (byte)50, (byte)116, (byte)0}, /*52*/
		{(byte)156, (byte)31, (byte)76, (byte)198, (byte)31, (byte)101, (byte)59, (byte)153, (byte)8, (byte)235, (byte)201, (byte)128, (byte)80, (byte)215, (byte)108, (byte)120, (byte)43, (byte)122, (byte)25, (byte)123, (byte)79, (byte)172, (byte)175, (byte)238, (byte)254, (byte)35, (byte)245, (byte)52, (byte)192, (byte)184, (byte)95, (byte)26, (byte)165, (byte)109, (byte)218, (byte)209, (byte)58, (byte)102, (byte)225, (byte)249, (byte)184, (byte)238, (byte)50, (byte)45, (byte)65, (byte)46, (byte)21, (byte)113, (byte)221, (byte)210, (byte)87, (byte)201, (byte)26, (byte)183, (byte)0}, /*54*/
		{10, (byte)61, (byte)20, (byte)207, (byte)202, (byte)154, (byte)151, (byte)247, (byte)196, (byte)27, (byte)61, (byte)163, (byte)23, (byte)96, (byte)206, (byte)152, (byte)124, (byte)101, (byte)184, (byte)239, (byte)85, (byte)10, (byte)28, (byte)190, (byte)174, (byte)177, (byte)249, (byte)182, (byte)142, (byte)127, (byte)139, (byte)12, (byte)209, (byte)170, (byte)208, (byte)135, (byte)155, (byte)254, (byte)144, (byte)6, (byte)229, (byte)202, (byte)201, (byte)36, (byte)163, (byte)248, (byte)91, (byte)2, (byte)116, (byte)112, (byte)216, (byte)164, (byte)157, (byte)107, (byte)120, (byte)106, (byte)0},  /*56*/
		{123, (byte)148, (byte)125, (byte)233, (byte)142, (byte)159, (byte)63, (byte)41, (byte)29, (byte)117, (byte)245, (byte)206, (byte)134, (byte)127, (byte)145, (byte)29, (byte)218, (byte)129, (byte)6, (byte)214, (byte)240, (byte)122, (byte)30, (byte)24, (byte)23, (byte)125, (byte)165, (byte)65, (byte)142, (byte)253, (byte)85, (byte)206, (byte)249, (byte)152, (byte)248, (byte)192, (byte)141, (byte)176, (byte)237, (byte)154, (byte)144, (byte)210, (byte)242, (byte)251, (byte)55, (byte)235, (byte)185, (byte)200, (byte)182, (byte)252, (byte)107, (byte)62, (byte)27, (byte)66, (byte)247, (byte)26, (byte)116, (byte)82, (byte)0},  /*58*/
		{(byte)240, (byte)33, (byte)7, (byte)89, (byte)16, (byte)209, (byte)27, (byte)70, (byte)220, (byte)190, (byte)102, (byte)65, (byte)87, (byte)194, (byte)25, (byte)84, (byte)181, (byte)30, (byte)124, (byte)11, (byte)86, (byte)121, (byte)209, (byte)160, (byte)49, (byte)238, (byte)38, (byte)37, (byte)82, (byte)160, (byte)109, (byte)101, (byte)219, (byte)115, (byte)57, (byte)198, (byte)205, (byte)2, (byte)247, (byte)100, (byte)6, (byte)127, (byte)181, (byte)28, (byte)120, (byte)219, (byte)101, (byte)211, (byte)45, (byte)219, (byte)197, (byte)226, (byte)197, (byte)243, (byte)141, (byte)9, (byte)12, (byte)26, (byte)140, (byte)107, (byte)0},  /*60*/
		{106, (byte)110, (byte)186, (byte)36, (byte)215, (byte)127, (byte)218, (byte)182, (byte)246, (byte)26, (byte)100, (byte)200, (byte)6, (byte)115, (byte)40, (byte)213, (byte)123, (byte)147, (byte)149, (byte)229, (byte)11, (byte)235, (byte)117, (byte)221, (byte)35, (byte)181, (byte)126, (byte)212, (byte)17, (byte)194, (byte)111, (byte)70, (byte)50, (byte)72, (byte)89, (byte)223, (byte)76, (byte)70, (byte)118, (byte)243, (byte)78, (byte)135, (byte)105, (byte)7, (byte)121, (byte)58, (byte)228, (byte)2, (byte)23, (byte)37, (byte)122, (byte)0, (byte)94, (byte)214, (byte)118, (byte)248, (byte)223, (byte)71, (byte)98, (byte)113, (byte)202, (byte)65, (byte)0}, /*62*/
		{(byte)231, (byte)213, (byte)156, (byte)217, (byte)243, (byte)178, (byte)11, (byte)204, (byte)31, (byte)242, (byte)230, (byte)140, (byte)108, (byte)99, (byte)63, (byte)238, (byte)242, (byte)125, (byte)195, (byte)195, (byte)140, (byte)47, (byte)146, (byte)184, (byte)47, (byte)91, (byte)216, (byte)4, (byte)209, (byte)218, (byte)150, (byte)208, (byte)156, (byte)145, (byte)24, (byte)29, (byte)212, (byte)199, (byte)93, (byte)160, (byte)53, (byte)127, (byte)26, (byte)119, (byte)149, (byte)141, (byte)78, (byte)200, (byte)254, (byte)187, (byte)204, (byte)177, (byte)123, (byte)92, (byte)119, (byte)68, (byte)49, (byte)159, (byte)158, (byte)7, (byte)9, (byte)175, (byte)51, (byte)45, (byte)0}, /*64*/
		{105, (byte)45, (byte)93, (byte)132, (byte)25, (byte)171, (byte)106, (byte)67, (byte)146, (byte)76, (byte)82, (byte)168, (byte)50, (byte)106, (byte)232, (byte)34, (byte)77, (byte)217, (byte)126, (byte)240, (byte)253, (byte)80, (byte)87, (byte)63, (byte)143, (byte)121, (byte)40, (byte)236, (byte)111, (byte)77, (byte)154, (byte)44, (byte)7, (byte)95, (byte)197, (byte)169, (byte)214, (byte)72, (byte)41, (byte)101, (byte)95, (byte)111, (byte)68, (byte)178, (byte)137, (byte)65, (byte)173, (byte)95, (byte)171, (byte)197, (byte)247, (byte)139, (byte)17, (byte)81, (byte)215, (byte)13, (byte)117, (byte)46, (byte)51, (byte)162, (byte)136, (byte)136, (byte)180, (byte)222, (byte)118, (byte)5, (byte)0},  /*66*/
		{(byte)238, (byte)163, (byte)8, (byte)5, (byte)3, (byte)127, (byte)184, (byte)101, (byte)27, (byte)235, (byte)238, (byte)43, (byte)198, (byte)175, (byte)215, (byte)82, (byte)32, (byte)54, (byte)2, (byte)118, (byte)225, (byte)166, (byte)241, (byte)137, (byte)125, (byte)41, (byte)177, (byte)52, (byte)231, (byte)95, (byte)97, (byte)199, (byte)52, (byte)227, (byte)89, (byte)160, (byte)173, (byte)253, (byte)84, (byte)15, (byte)84, (byte)93, (byte)151, (byte)203, (byte)220, (byte)165, (byte)202, (byte)60, (byte)52, (byte)133, (byte)205, (byte)190, (byte)101, (byte)84, (byte)150, (byte)43, (byte)254, (byte)32, (byte)160, (byte)90, (byte)70, (byte)77, (byte)93, (byte)224, (byte)33, (byte)223, (byte)159, (byte)247, (byte)0},  /*68*/
	};


	public static byte[] expToInt = {
		1, (byte)
		2, (byte)
		4, (byte)
		8, (byte)
		16, (byte)
		32, (byte)
		64, (byte)
		128, (byte)
		29, (byte)
		58, (byte)
		116, (byte)
		232, (byte)
		205, (byte)
		135, (byte)
		19, (byte)
		38, (byte)
		76, (byte)
		152, (byte)
		45, (byte)
		90, (byte)
		180, (byte)
		117, (byte)
		234, (byte)
		201, (byte)
		143, (byte)
		3, (byte)
		6, (byte)
		12, (byte)
		24, (byte)
		48, (byte)
		96, (byte)
		192, (byte)
		157, (byte)
		39, (byte)
		78, (byte)
		156, (byte)
		37, (byte)
		74, (byte)
		148, (byte)
		53, (byte)
		106, (byte)
		212, (byte)
		181, (byte)
		119, (byte)
		238, (byte)
		193, (byte)
		159, (byte)
		35, (byte)
		70, (byte)
		140, (byte)
		5, (byte)
		10, (byte)
		20, (byte)
		40, (byte)
		80, (byte)
		160, (byte)
		93, (byte)
		186, (byte)
		105, (byte)
		210, (byte)
		185, (byte)
		111, (byte)
		222, (byte)
		161, (byte)
		95, (byte)
		190, (byte)
		97, (byte)
		194, (byte)
		153, (byte)
		47, (byte)
		94, (byte)
		188, (byte)
		101, (byte)
		202, (byte)
		137, (byte)
		15, (byte)
		30, (byte)
		60, (byte)
		120, (byte)
		240, (byte)
		253, (byte)
		231, (byte)
		211, (byte)
		187, (byte)
		107, (byte)
		214, (byte)
		177, (byte)
		127, (byte)
		254, (byte)
		225, (byte)
		223, (byte)
		163, (byte)
		91, (byte)
		182, (byte)
		113, (byte)
		226, (byte)
		217, (byte)
		175, (byte)
		67, (byte)
		134, (byte)
		17, (byte)
		34, (byte)
		68, (byte)
		136, (byte)
		13, (byte)
		26, (byte)
		52, (byte)
		104, (byte)
		208, (byte)
		189, (byte)
		103, (byte)
		206, (byte)
		129, (byte)
		31, (byte)
		62, (byte)
		124, (byte)
		248, (byte)
		237, (byte)
		199, (byte)
		147, (byte)
		59, (byte)
		118, (byte)
		236, (byte)
		197, (byte)
		151, (byte)
		51, (byte)
		102, (byte)
		204, (byte)
		133, (byte)
		23, (byte)
		46, (byte)
		92, (byte)
		184, (byte)
		109, (byte)
		218, (byte)
		169, (byte)
		79, (byte)
		158, (byte)
		33, (byte)
		66, (byte)
		132, (byte)
		21, (byte)
		42, (byte)
		84, (byte)
		168, (byte)
		77, (byte)
		154, (byte)
		41, (byte)
		82, (byte)
		164, (byte)
		85, (byte)
		170, (byte)
		73, (byte)
		146, (byte)
		57, (byte)
		114, (byte)
		228, (byte)
		213, (byte)
		183, (byte)
		115, (byte)
		230, (byte)
		209, (byte)
		191, (byte)
		99, (byte)
		198, (byte)
		145, (byte)
		63, (byte)
		126, (byte)
		252, (byte)
		229, (byte)
		215, (byte)
		179, (byte)
		123, (byte)
		246, (byte)
		241, (byte)
		255, (byte)
		227, (byte)
		219, (byte)
		171, (byte)
		75, (byte)
		150, (byte)
		49, (byte)
		98, (byte)
		196, (byte)
		149, (byte)
		55, (byte)
		110, (byte)
		220, (byte)
		165, (byte)
		87, (byte)
		174, (byte)
		65, (byte)
		130, (byte)
		25, (byte)
		50, (byte)
		100, (byte)
		200, (byte)
		141, (byte)
		7, (byte)
		14, (byte)
		28, (byte)
		56, (byte)
		112, (byte)
		224, (byte)
		221, (byte)
		167, (byte)
		83, (byte)
		166, (byte)
		81, (byte)
		162, (byte)
		89, (byte)
		178, (byte)
		121, (byte)
		242, (byte)
		249, (byte)
		239, (byte)
		195, (byte)
		155, (byte)
		43, (byte)
		86, (byte)
		172, (byte)
		69, (byte)
		138, (byte)
		9, (byte)
		18, (byte)
		36, (byte)
		72, (byte)
		144, (byte)
		61, (byte)
		122, (byte)
		244, (byte)
		245, (byte)
		247, (byte)
		243, (byte)
		251, (byte)
		235, (byte)
		203, (byte)
		139, (byte)
		11, (byte)
		22, (byte)
		44, (byte)
		88, (byte)
		176, (byte)
		125, (byte)
		250, (byte)
		233, (byte)
		207, (byte)
		131, (byte)
		27, (byte)
		54, (byte)
		108, (byte)
		216, (byte)
		173, (byte)
		71, (byte)
		142, (byte)
		1
	};
	public static byte[] intToExp = {
		0, (byte) //note: there is no integer zero value - but I put it in to keep index the same as exponent
		0, (byte)
		1, (byte)
		25, (byte)
		2, (byte)
		50, (byte)
		26, (byte)
		198, (byte)
		3, (byte)
		223, (byte)
		51, (byte)
		238, (byte)
		27, (byte)
		104, (byte)
		199, (byte)
		75, (byte)
		4, (byte)
		100, (byte)
		224, (byte)
		14, (byte)
		52, (byte)
		141, (byte)
		239, (byte)
		129, (byte)
		28, (byte)
		193, (byte)
		105, (byte)
		248, (byte)
		200, (byte)
		8, (byte)
		76, (byte)
		113, (byte)
		5, (byte)
		138, (byte)
		101, (byte)
		47, (byte)
		225, (byte)
		36, (byte)
		15, (byte)
		33, (byte)
		53, (byte)
		147, (byte)
		142, (byte)
		218, (byte)
		240, (byte)
		18, (byte)
		130, (byte)
		69, (byte)
		29, (byte)
		181, (byte)
		194, (byte)
		125, (byte)
		106, (byte)
		39, (byte)
		249, (byte)
		185, (byte)
		201, (byte)
		154, (byte)
		9, (byte)
		120, (byte)
		77, (byte)
		228, (byte)
		114, (byte)
		166, (byte)
		6, (byte)
		191, (byte)
		139, (byte)
		98, (byte)
		102, (byte)
		221, (byte)
		48, (byte)
		253, (byte)
		226, (byte)
		152, (byte)
		37, (byte)
		179, (byte)
		16, (byte)
		145, (byte)
		34, (byte)
		136, (byte)
		54, (byte)
		208, (byte)
		148, (byte)
		206, (byte)
		143, (byte)
		150, (byte)
		219, (byte)
		189, (byte)
		241, (byte)
		210, (byte)
		19, (byte)
		92, (byte)
		131, (byte)
		56, (byte)
		70, (byte)
		64, (byte)
		30, (byte)
		66, (byte)
		182, (byte)
		163, (byte)
		195, (byte)
		72, (byte)
		126, (byte)
		110, (byte)
		107, (byte)
		58, (byte)
		40, (byte)
		84, (byte)
		250, (byte)
		133, (byte)
		186, (byte)
		61, (byte)
		202, (byte)
		94, (byte)
		155, (byte)
		159, (byte)
		10, (byte)
		21, (byte)
		121, (byte)
		43, (byte)
		78, (byte)
		212, (byte)
		229, (byte)
		172, (byte)
		115, (byte)
		243, (byte)
		167, (byte)
		87, (byte)
		7, (byte)
		112, (byte)
		192, (byte)
		247, (byte)
		140, (byte)
		128, (byte)
		99, (byte)
		13, (byte)
		103, (byte)
		74, (byte)
		222, (byte)
		237, (byte)
		49, (byte)
		197, (byte)
		254, (byte)
		24, (byte)
		227, (byte)
		165, (byte)
		153, (byte)
		119, (byte)
		38, (byte)
		184, (byte)
		180, (byte)
		124, (byte)
		17, (byte)
		68, (byte)
		146, (byte)
		217, (byte)
		35, (byte)
		32, (byte)
		137, (byte)
		46, (byte)
		55, (byte)
		63, (byte)
		209, (byte)
		91, (byte)
		149, (byte)
		188, (byte)
		207, (byte)
		205, (byte)
		144, (byte)
		135, (byte)
		151, (byte)
		178, (byte)
		220, (byte)
		252, (byte)
		190, (byte)
		97, (byte)
		242, (byte)
		86, (byte)
		211, (byte)
		171, (byte)
		20, (byte)
		42, (byte)
		93, (byte)
		158, (byte)
		132, (byte)
		60, (byte)
		57, (byte)
		83, (byte)
		71, (byte)
		109, (byte)
		65, (byte)
		162, (byte)
		31, (byte)
		45, (byte)
		67, (byte)
		216, (byte)
		183, (byte)
		123, (byte)
		164, (byte)
		118, (byte)
		196, (byte)
		23, (byte)
		73, (byte)
		236, (byte)
		127, (byte)
		12, (byte)
		111, (byte)
		246, (byte)
		108, (byte)
		161, (byte)
		59, (byte)
		82, (byte)
		41, (byte)
		157, (byte)
		85, (byte)
		170, (byte)
		251, (byte)
		96, (byte)
		134, (byte)
		177, (byte)
		187, (byte)
		204, (byte)
		62, (byte)
		90, (byte)
		203, (byte)
		89, (byte)
		95, (byte)
		176, (byte)
		156, (byte)
		169, (byte)
		160, (byte)
		81, (byte)
		11, (byte)
		245, (byte)
		22, (byte)
		235, (byte)
		122, (byte)
		117, (byte)
		44, (byte)
		215, (byte)
		79, (byte)
		174, (byte)
		213, (byte)
		233, (byte)
		230, (byte)
		231, (byte)
		173, (byte)
		232, (byte)
		116, (byte)
		214, (byte)
		244, (byte)
		234, (byte)
		168, (byte)
		80, (byte)
		88, (byte)
		175		
	};
}	
