package mturk.util;

import java.util.HashSet;
import java.util.Set;

import utils.Stemmer;

import java.util.Arrays;

public class Stopwords {
	
	public static String[]spanishStopwords = {"a","actualmente","acuerdo","adelante","ademas","además","adrede","afirmó","agregó","ahi","ahora","ahí","al","algo","alguna",
			"algunas","alguno","algunos","algún","alli","allí","alrededor","ambos","ampleamos","antano","antaño","ante","anterior","antes","apenas","aproximadamente",
			"aquel","aquella","aquellas","aquello","aquellos","aqui","aquél","aquélla","aquéllas","aquéllos","aquí","arriba","arribaabajo","aseguró","asi","así",
			"atras","aun","aunque","ayer","añadió","aún","b","bajo","bastante","bien","breve","buen","buena","buenas","bueno","buenos","c","cada","casi","cerca",
			"cierta","ciertas","cierto","ciertos","cinco","claro","comentó","como","con","conmigo","conocer","conseguimos","conseguir","considera","consideró","consigo",
			"consigue","consiguen","consigues","contigo","contra","cosas","creo","cual","cuales","cualquier","cuando","cuanta","cuantas","cuanto","cuantos","cuatro",
			"cuenta","cuál","cuáles","cuándo","cuánta","cuántas","cuánto","cuántos","cómo","d","da","dado","dan","dar","de","debajo","debe","deben","debido","decir",
			"dejó","del","delante","demasiado","demás","dentro","deprisa","desde","despacio","despues","después","detras","detrás","dia","dias","dice","dicen","dicho",
			"dieron","diferente","diferentes","dijeron","dijo","dio","donde","dos","durante","día","días","dónde","e","ejemplo","el","ella","ellas","ello","ellos",
			"embargo","empleais","emplean","emplear","empleas","empleo","en","encima","encuentra","enfrente","enseguida","entonces","entre","era","eramos","eran",
			"eras","eres","es","esa","esas","ese","eso","esos","esta","estaba","estaban","estado","estados","estais","estamos","estan","estar","estará","estas","este",
			"esto","estos","estoy","estuvo","está","están","ex","excepto","existe","existen","explicó","expresó","f","fin","final","fue","fuera","fueron","fui","fuimos",
			"g","general","gran","grandes","gueno","h","ha","haber","habia","habla","hablan","habrá","había","habían","hace","haceis","hacemos","hacen","hacer","hacerlo",
			"haces","hacia","haciendo","hago","han","hasta","hay","haya","he","hecho","hemos","hicieron","hizo","horas","hoy","hubo","i","igual","incluso","indicó",
			"informo","informó","intenta","intentais","intentamos","intentan","intentar","intentas","intento","ir","j","junto","k","l","la","lado","largo","las","le",
			"lejos","les","llegó","lleva","llevar","lo","los","luego","lugar","m","mal","manera","manifestó","mas","mayor","me","mediante","medio","mejor","mencionó",
			"menos","menudo","mi","mia","mias","mientras","mio","mios","mis","misma","mismas","mismo","mismos","modo","momento","mucha","muchas","mucho","muchos","muy",
			"más","mí","mía","mías","mío","míos","n","nada","nadie","ni","ninguna","ningunas","ninguno","ningunos","ningún","no","nos","nosotras","nosotros","nuestra",
			"nuestras","nuestro","nuestros","nueva","nuevas","nuevo","nuevos","nunca","o","ocho","os","otra","otras","otro","otros","p","pais","para","parece","parte",
			"partir","pasada","pasado","paìs","peor","pero","pesar","poca","pocas","poco","pocos","podeis","podemos","poder","podria","podriais","podriamos","podrian",
			"podrias","podrá","podrán","podría","podrían","poner","por","porque","posible","primer","primera","primero","primeros","principalmente","pronto","propia",
			"propias","propio","propios","proximo","próximo","próximos","pudo","pueda","puede","pueden","puedo","pues","q","qeu","que","quedó","queremos","quien",
			"quienes","quiere","quiza","quizas","quizá","quizás","quién","quiénes","qué","r","raras","realizado","realizar","realizó","repente","respecto","s","sabe",
			"sabeis","sabemos","saben","saber","sabes","salvo","se","sea","sean","segun","segunda","segundo","según","seis","ser","sera","será","serán","sería","señaló",
			"si","sido","siempre","siendo","siete","sigue","siguiente","sin","sino","sobre","sois","sola","solamente","solas","solo","solos","somos","son","soy","soyos",
			"su","supuesto","sus","suya","suyas","suyo","sé","sí","sólo","t","tal","tambien","también","tampoco","tan","tanto","tarde","te","temprano","tendrá","tendrán",
			"teneis","tenemos","tener","tenga","tengo","tenido","tenía","tercera","ti","tiempo","tiene","tienen","toda","todas","todavia","todavía","todo","todos",
			"total","trabaja","trabajais","trabajamos","trabajan","trabajar","trabajas","trabajo","tras","trata","través","tres","tu","tus","tuvo","tuya","tuyas","tuyo",
			"tuyos","tú","u","ultimo","un","una","unas","uno","unos","usa","usais","usamos","usan","usar","usas","uso","usted","ustedes","v","va","vais","valor",
			"vamos","van","varias","varios","vaya","veces","ver","verdad","verdadera","verdadero","vez","vosotras","vosotros","voy","vuestra","vuestras","vuestro",
			"vuestros","w","x","y","ya","yo","z","él","ésa","ésas","ése","ésos","ésta","éstas","éste","éstos","última","últimas","último","últimos"};
	
	public static String[]germanStopwords = {"aber","als","am","an","auch","auf","aus","bei","bin","bis","bist","da","dadurch","daher","darum","das","daß","dass","dein","deine","dem","den",
			"der","des","dessen","deshalb","die","dies","dieser","dieses","doch","dort","du","durch","ein","eine","einem","einen","einer","eines","er","es","euer","eure","für","hatte",
			"hatten","hattest","hattet","hier","hinter","ich","ihr","ihre","im","in","ist","ja","jede","jedem","jeden","jeder","jedes","jener","jenes","jetzt","kann","kannst","können",
			"könnt","machen","mein","meine","mit","muß","mußt","musst","müssen","müßt","nach","nachdem","nein","nicht","nun","oder","seid","sein","seine","sich","sie","sind","soll",
			"sollen","sollst","sollt","sonst","soweit","sowie","und","unser","unsere","unter","vom","von","vor","wann","warum","was","weiter","weitere","wenn","wer","werde","werden",
			"werdet","weshalb","wie","wieder","wieso","wir","wird","wirst","wo","woher","wohin","zu","zum","zur","über","ab","aber","alle","allein","allem","allen","aller","allerdings",
			"allerlei","alles","allmählich","allzu","als","alsbald","also","am","an","and","ander","andere","anderem","anderen","anderer","andererseits","anderes","anderm","andern",
			"andernfalls","anders","anstatt","auch","auf","aus","ausgenommen","ausser","ausserdem","außer","außerdem","außerhalb","bald","bei","beide","beiden","beiderlei","beides",
			"beim","beinahe","bereits","besonders","besser","beträchtlich","bevor","bezüglich","bin","bis","bisher","bislang","bist","bloß","bsp.","bzw","ca","ca.","content","da","dabei",
			"dadurch","dafür","dagegen","daher","dahin","damals","damit","danach","daneben","dann","daran","darauf","daraus","darin","darum","darunter","darüber","darüberhinaus","das",
			"dass","dasselbe","davon","davor","dazu","daß","dein","deine","deinem","deinen","deiner","deines","dem","demnach","demselben","den","denen","denn","dennoch","denselben","der",
			"derart","derartig","derem","deren","derer","derjenige","derjenigen","derselbe","derselben","derzeit","des","deshalb","desselben","dessen","desto","deswegen","dich","die",
			"diejenige","dies","diese","dieselbe","dieselben","diesem","diesen","dieser","dieses","diesseits","dir","direkt","direkte","direkten","direkter","doch","dort","dorther",
			"dorthin","drauf","drin","drunter","drüber","du","dunklen","durch","durchaus","eben","ebenfalls","ebenso","eher","eigenen","eigenes","eigentlich","ein","eine","einem","einen",
			"einer","einerseits","eines","einfach","einführen","einführte","einführten","eingesetzt","einig","einige","einigem","einigen","einiger","einigermaßen","einiges","einmal",
			"eins","einseitig","einseitige","einseitigen","einseitiger","einst","einstmals","einzig","entsprechend","entweder","er","erst","es","etc","etliche","etwa","etwas","euch",
			"euer","eure","eurem","euren","eurer","eures","falls","fast","ferner","folgende","folgenden","folgender","folgendes","folglich","fuer","für","gab","ganze","ganzem","ganzen",
			"ganzer","ganzes","gar","gegen","gemäss","ggf","gleich","gleichwohl","gleichzeitig","glücklicherweise","gänzlich","hab","habe","haben","haette","hast","hat","hatte","hatten",
			"hattest","hattet","heraus","herein","hier","hier	hinter","hiermit","hiesige","hin","hinein","hinten","hinter","hinterher","http","hätt","hätte","hätten","höchstens","ich",
			"igitt","ihm","ihn","ihnen","ihr","ihre","ihrem","ihren","ihrer","ihres","im","immer","immerhin","in","indem","indessen","infolge","innen","innerhalb","ins","insofern",
			"inzwischen","irgend","irgendeine","irgendwas","irgendwen","irgendwer","irgendwie","irgendwo","ist","ja","je","jed","jede","jedem","jeden","jedenfalls","jeder","jederlei",
			"jedes","jedoch","jemand","jene","jenem","jenen","jener","jenes","jenseits","jetzt","jährig","jährige","jährigen","jähriges","kam","kann","kannst","kaum","kein","keine",
			"keinem","keinen","keiner","keinerlei","keines","keineswegs","klar","klare","klaren","klares","klein","kleinen","kleiner","kleines","koennen","koennt","koennte","koennten",
			"komme","kommen","kommt","konkret","konkrete","konkreten","konkreter","konkretes","können","könnt","künftig","leider","machen","man","manche","manchem","manchen","mancher",
			"mancherorts","manches","manchmal","mehr","mehrere","mein","meine","meinem","meinen","meiner","meines","mich","mir","mit","mithin","muessen","muesst","muesste","muss","musst",
			"musste","mussten","muß","mußt","müssen","müsste","müssten","müßt","müßte","nach","nachdem","nachher","nachhinein","nahm","natürlich","neben","nebenan","nehmen","nein","nicht",
			"nichts","nie","niemals","niemand","nirgends","nirgendwo","noch","nun","nur","nächste","nämlich","nötigenfalls","ob","oben","oberhalb","obgleich","obschon","obwohl","oder",
			"oft","per","plötzlich","schließlich","schon","sehr","sehrwohl","seid","sein","seine","seinem","seinen","seiner","seines","seit","seitdem","seither","selber","selbst","sich",
			"sicher","sicherlich","sie","sind","so","sobald","sodass","sodaß","soeben","sofern","sofort","sogar","solange","solch","solche","solchem","solchen","solcher","solches","soll",
			"sollen","sollst","sollt","sollte","sollten","solltest","somit","sondern","sonst","sonstwo","sooft","soviel","soweit","sowie","sowohl","tatsächlich","tatsächlichen",
			"tatsächlicher","tatsächliches","trotzdem","ueber","um","umso","unbedingt","und","unmöglich","unmögliche","unmöglichen","unmöglicher","uns","unser","unser	unsere",
			"unsere","unserem","unseren","unserer","unseres","unter","usw","viel","viele","vielen","vieler","vieles","vielleicht","vielmals","vom","von","vor","voran","vorher",
			"vorüber","völlig","wann","war","waren","warst","warum","was","weder","weil","weiter","weitere","weiterem","weiteren","weiterer","weiteres","weiterhin","weiß","welche",
			"welchem","welchen","welcher","welches","wem","wen","wenig","wenige","weniger","wenigstens","wenn","wenngleich","wer","werde","werden","werdet","weshalb","wessen","wichtig",
			"wie","wieder","wieso","wieviel","wiewohl","will","willst","wir","wird","wirklich","wirst","wo","wodurch","wogegen","woher","wohin","wohingegen","wohl","wohlweislich","womit",
			"woraufhin","woraus","worin","wurde","wurden","während","währenddessen","wär","wäre","wären","würde","würden","z.B.","zB","zahlreich","zeitweise","zu","zudem","zuerst",
			"zufolge","zugleich","zuletzt","zum","zumal","zur","zurück","zusammen","zuviel","zwar","zwischen","ähnlich","übel","über","überall","überallhin","überdies","übermorgen",
			"übrig","übrigens"};
	public static String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", 
	                "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along",
	                "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody",
	                "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate",
	                "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", 
	                "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being",
	                "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by",
	                "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes",
	                "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain",
	                "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described",
	                "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", 
	                "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et",
	                "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", 
	                "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former",
	                "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go",
	                "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt",
	                "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby",
	                "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit",
	                "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", 
	                "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd",
	                "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately",
	                "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", 
	                "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", 
	                "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly",
	                "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non",
	                "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", 
	                "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", 
	                "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular",
	                "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides",
	                "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards",
	                "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly",
	                "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious",
	                "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody",
	                "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified",
	                "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th",
	                "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves",
	                "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", 
	                "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly",
	                "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", 
	                "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless",
	                "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", 
	                "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were",
	                "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever",
	                "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", 
	                "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with",
	                "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre",
	                "youve", "your", "yours", "yourself", "yourselves", "zero"};

	public static Set<String> stopWordSet = new HashSet<String>(Arrays.asList(stopwords));
	public static Set<String> spanishStopWordSet = new HashSet<String>(Arrays.asList(spanishStopwords));
	public static Set<String> germanStopWordSet = new HashSet<String>(Arrays.asList(germanStopwords));
	
	public static Set<String> stemmedStopWordSet = stemStringSet(stopWordSet);
	
	public static boolean isStopword(String word) {
		if(word.length() < 2) return true;
		if(word.charAt(0) >= '0' && word.charAt(0) <= '9') return true; //remove numbers, "25th", etc
		if(stopWordSet.contains(word)) return true;
		else return false;
	}
	
	public static boolean isSpanishStopword(String word) {
		if(word.length() < 2) return true;
		if(word.charAt(0) >= '0' && word.charAt(0) <= '9') return true; //remove numbers, "25th", etc
		if(spanishStopWordSet.contains(word)) return true;
		else return false;
	}
	
	public static boolean isGermanStopwords(String word) {
		if(word.length() < 2) return true;
		if(word.charAt(0) >= '0' && word.charAt(0) <= '9') return true; //remove numbers, "25th", etc
		if(germanStopWordSet.contains(word)) return true;
		else return false;
	}
	
	public static boolean isStemmedStopword(String word) {
		if(word.length() < 2) return true;
		if(word.charAt(0) >= '0' && word.charAt(0) <= '9') return true; //remove numbers, "25th", etc
		String stemmed = stemString(word);
		if(stopWordSet.contains(stemmed)) return true;
		if(stemmedStopWordSet.contains(stemmed)) return true;
		if(stopWordSet.contains(word)) return true;
		if(stemmedStopWordSet.contains(word)) return true;
		else return false;
	}
	
	public static String removeStopWords(String string) {
		String result = "";
		String[] words = string.split("\\s+");
		for(String word : words) {
			if(word.isEmpty()) continue;
			if(isStopword(string)) continue; //remove stopwords
			result += (word+" ");
		}
		return result;
	}
	
	public static String removeStemmedStopWords(String string) {
		String result = "";
		String[] words = string.split("\\s+");
		for(String word : words) {
			if(word.isEmpty()) continue;
			if(isStemmedStopword(word)) continue;
			if(word.charAt(0) >= '0' && word.charAt(0) <= '9') continue; //remove numbers, "25th", etc
			result += (word+" ");
		}
		return result;
	}
	
	public static String stemString(String string) {
		return new Stemmer().stem(string);
	}
	
	public static Set<String> stemStringSet(Set<String> stringSet) {
		Stemmer stemmer = new Stemmer();
		Set<String> results = new HashSet<String>();
		for(String string : stringSet) {
			results.add(stemmer.stem(string));
		}
		return results;
	}
}