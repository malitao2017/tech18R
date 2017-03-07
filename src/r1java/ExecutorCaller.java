package r1java;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

public class ExecutorCaller {
	public static void main(String[] args) {
		ExecutorCaller call = new ExecutorCaller();
		System.out.println(call.execute());
	}
	
	public static Lock lock = new ReentrantLock();
	public String execute(){		
		lock.lock();
		Rengine re = ExecutorCaller.getInstance();
        try{
    		return this.doExecute(re);
        }finally{
        	re.end();
        	lock.unlock();
        }
 	}
	public String doExecute(Rengine re) {
		System.out.println("kaishi ");
//		String console = re.jriReadConsole("控制台输入了：",0);
//		System.out.println(console);
		re.eval("print('hello world')");
		double d = re.eval("x<-2*40").asDouble();
		System.out.println("执行："+d);
		String xVector = "(1,2,34,54)";
        re.eval("xx <- c"+xVector);
        re.eval("x <- seq(from=min(xx),to=max(xx),length.out=2)").asDoubleArray();
        String mean = "12";re.eval("int<-"+mean);
        String sd = "22";re.eval("slope<-"+sd);
        re.eval("f <- function(x){ slope*x+int }");
        double[] y = re.eval("y <- f(x)").asDoubleArray();
        System.out.println("计算的函数y=a*x+b 为："+y[0]+","+y[1]);
        return "执行完成";
    }
	
	public ExecutorCaller(){}//构造函数
	private static Rengine re ;//要求单例，R语言的特性是，每个变量的运行都能全局通用
	public static Rengine getInstance(){
		if(re == null){
			re = new Rengine(null, false, new PiplineStreamConsole());
			re.eval("library(arules)");
		}
		return re;
	}
	public static class PiplineStreamConsole implements RMainLoopCallbacks {
		public void rWriteConsole(Rengine re, String text, int oType) {
			System.out.print(text);
		}

		public void rBusy(Rengine re, int which) {
			System.out.println("rBusy(" + which + ")");
		}

		public String rReadConsole(Rengine re, String prompt, int addToHistory) {
			System.out.print(prompt);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String s = br.readLine();
				return (s == null || s.length() == 0) ? s : s + "\n";
			} catch (Exception e) {
				System.out.println("jriReadConsole exception: " + e.getMessage());
			}
			return null;
		}

		public void rShowMessage(Rengine re, String message) {
			System.out.println("rShowMessage \"" + message + "\"");
		}

		@SuppressWarnings("deprecation")
		public String rChooseFile(Rengine re, int newFile) {
			FileDialog fd = new FileDialog(new Frame(), (newFile == 0) ? "Select a file" : "Select a new file",
					(newFile == 0) ? FileDialog.LOAD : FileDialog.SAVE);
			fd.show();
			String res = null;
			if (fd.getDirectory() != null)
				res = fd.getDirectory();
			if (fd.getFile() != null)
				res = (res == null) ? fd.getFile() : (res + fd.getFile());
			return res;
		}

		public void rFlushConsole(Rengine re) {
		}

		public void rLoadHistory(Rengine re, String filename) {
		}

		public void rSaveHistory(Rengine re, String filename) {
		}
	}
	
}
