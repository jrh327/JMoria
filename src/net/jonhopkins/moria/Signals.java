/*
 * Signals.java: signal handlers
 * 
 * Copyright (C) 1989-2008 James E. Wilson, Robert A. Koeneke, 
 *                         David J. Grabiner
 * 
 * This file is part of Umoria.
 * 
 * Umoria is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Umoria is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with Umoria.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jonhopkins.moria;

public class Signals {
	/* This signal package was brought to you by		-JEW-  */
	/* Completely rewritten by				-CJS- */
	/* Modified for Java by					-JRH- */
	private int error_sig = -1;
	private int signal_count = 0;
	
	private IO io;
	private Moria1 mor1;
	private Variable var;
	
	private static Signals instance;
	private Signals() { }
	public static Signals getInstance() {
		if (instance == null) {
			instance = new Signals();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		io = IO.getInstance();
		mor1 = Moria1.getInstance();
		var = Variable.getInstance();
	}
	
	/*
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class SignalHandlerExample implements SignalHandler {

    private SignalHandler oldHandler;

    public static SignalHandler install(String signalName) {
        Signal diagSignal = new Signal(signalName);
        SignalHandlerExample instance = new SignalHandlerExample();
        instance.oldHandler = Signal.handle(diagSignal, instance);
        return instance;
    }

    public void handle(Signal signal) {
        System.out.println("Signal handler called for signal "
              + signal);
        try {

            signalAction(signal);

            // Chain back to previous handler, if one exists
            if (oldHandler != SIG_DFL && oldHandler != SIG_IGN) {
                oldHandler.handle(signal);
            }

        } catch (Exception e) {
            System.out.println("handle|Signal handler
                 failed, reason " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void signalAction(Signal signal) {
        System.out.println("Handling " + signal.getName());
        System.out.println("Just sleep for 5 seconds.");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("Interrupted: "
              + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SignalHandlerExample.install("TERM");
        SignalHandlerExample.install("INT");
        SignalHandlerExample.install("ABRT");

        System.out.println("Signal handling example.");
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            System.out.println("Interrupted: " + e.getMessage());
        }

    }
}
	 */
	
	/*ARGSUSED*/
	public void signal_handler(int sig) {
	//	if(error_sig >= 0) {	/* Ignore all second signals. */
	//		if (++signal_count > 10) {	/* Be safe. We will die if persistent enough. */
	//			signal(sig, SIG_DFL);
	//		}
	//		return;
	//	}
	//	error_sig = sig;
	//	
	//	/* Allow player to think twice. Wizard may force a core dump. */
	//	if (sig == SIGINT || sig == SIGQUIT) {
	//		if (var.death) {
	//			signal(sig, SIG_IGN);		/* Can't quit after death. */
	//		} else if (var.character_saved == 0 && var.character_generated) {
	//			if (!io.get_check("Really commit *Suicide*?")) {
	//				if (var.turn > 0) {
	//					mor1.disturb(true, false);
	//				}
	//				io.erase_line(0, 0);
	//				io.put_qio();
	//				error_sig = -1;
	//				signal(sig, signal_handler);/* Have to restore handler. */
	//				/* in case control-c typed during msg_print */
	//				if (var.wait_for_more != 0) {
	//					io.put_buffer(" -more-", Constants.MSG_LINE, 0);
	//				}
	//				io.put_qio();
	//				return;		/* OK. We don't quit. */
	//			}
	//			var.died_from = "Interrupting";
	//		} else {
	//			var.died_from = "Abortion";
	//		}
	//		io.prt("Interrupt!", 0, 0);
	//		var.death = true;
	//		exit_game();
	//	}
	//	/* Die. */
	//	io.prt("OH NO!!!!!!  A gruesome software bug LEAPS out at you. There is NO defense!", 23, 0);
	//	if (!var.death && var.character_saved == 0 && var.character_generated) {
	//		var.panic_save = 1;
	//		io.prt("Your guardian angel is trying to save you.", 0, 0);
	//		var.died_from = String.format("(panic save %d)", sig);
	//		if (!save_char()) {
	//			var.died_from = "software bug";
	//			var.death = true;
	//			var.turn = -1;
	//		}
	//	} else {
	//		var.death = true;
	//		_save_char(savefile);	/* Quietly save the memory anyway. */
	//	}
	//	io.restore_term();
	//	/* always generate a core dump */
	//	signal(sig, SIG_DFL);
	//	kill(getpid(), sig);
	//	Thread.sleep(5);
	//	System.exit(1);
	}
	
	private int mask;
	
	public void nosignals() {
		/*
		signal(SIGTSTP, SIG_IGN);
		mask = sigsetmask(0);
		if (error_sig < 0) {
			error_sig = 0;
		}
		*/
	}
	
	public void signals() {
		/*
		signal(SIGTSTP, suspend);
		sigsetmask(mask);
		if (error_sig == 0) {
			error_sig = -1;
		}
		*/
	}
	
	public void init_signals() {
		/*
		signal(SIGINT, signal_handler);
		signal(SIGFPE, signal_handler);
		/* Ignore HANGUP, and let the EOF code take care of this case. */
		/*
		signal(SIGHUP, SIG_IGN);
		signal(SIGQUIT, signal_handler);
		signal(SIGILL, signal_handler);
		signal(SIGTRAP, signal_handler);
		signal(SIGIOT, signal_handler);
		signal(SIGKILL, signal_handler);
		signal(SIGBUS, signal_handler);
		signal(SIGSEGV, signal_handler);
		signal(SIGSYS, signal_handler);
		signal(SIGTERM, signal_handler);
		signal(SIGPIPE, signal_handler);
		*/
	}
	
	public void ignore_signals() {
		/*
		signal(SIGINT, SIG_IGN);
		signal(SIGQUIT, SIG_IGN);
		*/
	}
	
	public void default_signals() {
		/*
		signal(SIGINT, SIG_DFL);
		signal(SIGQUIT, SIG_DFL);
		*/
	}
	
	public void restore_signals() {
		/*
		signal(SIGINT, signal_handler);
		signal(SIGQUIT, signal_handler);
		*/
	}
}
