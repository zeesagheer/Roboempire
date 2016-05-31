
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;
import pl.autoempire.core.json.JSONArray;
import org.json.JSONException;

import pl.autoempire.core.CastlePosition;
import pl.autoempire.core.ResourcesCurrency;
import pl.autoempire.core.Utils;
import pl.autoempire.core.connection.ClientSettings;
import pl.autoempire.core.connection.DisconnectException;
import pl.autoempire.core.connection.LoginException;
import pl.autoempire.core.connection.WaitMsgTimeoutException;
import pl.autoempire.core.dict.CastleBattleArea;
import pl.autoempire.core.dict.DefenceToolUnit;
import pl.autoempire.core.dict.LiderType;
import pl.autoempire.core.dict.ResourceType;
import pl.autoempire.core.dict.SiegieToolUnit;
import pl.autoempire.core.dict.SoldierUnit;
import pl.autoempire.core.dict.SoldierWeaponType;
import pl.autoempire.core.messages.JSON.MsgOutCRA;
import pl.autoempire.core.objects.ObjectCRA;
import pl.autoempire.core.objects.ObjectEQItem;
import pl.autoempire.core.objects.ObjectGCA;
import pl.autoempire.core.objects.ObjectGUI;
import pl.autoempire.core.objects.ObjectIItem;
import pl.autoempire.core.objects.ObjectUM_L;
import pl.autoempire.core.servers.Server;
import pl.autoempire.core.servers.ServerSettings;
import pl.autoempire.gui.AppData;
import pl.autoempire.gui.resources.messages.Messages;
import pl.autoempire.gui.windows.MainWnd;

public class MainApp {
	static String[] castle = 
	{"Armed Brother",
	"Fire Arms",
	"Rage Thrower",
	"I am Hungry",
	"Fort Rage",
	"Burning Death",
	"Freezing Death",
	"Reaper Death",
	"BhaaiStorm"};
	static String selectedCastle = "Fort Rage";
	static ArrayList<Integer> rangeToKill = null;
	
	public static void main(String[] args) {
		connect("bhaai", "letmethink");
		int count = 1;
		boolean loop = false;
		rangeToKill = new ArrayList<Integer>();
		rangeToKill.add(19);
		rangeToKill.add(23);
		rangeToKill.add(693);
		// Scanner in = new Scanner(System.in);
		do {
			if (!AppData.session.isConnected())
				reconnect();
			printArmy(castle[7]);
			// System.out.println("count: "+count++);
			//pause(10);
			
		} while (loop);
		 try {
		 AppData.session.disconnect();
		 } catch (DisconnectException e) {
		 e.printStackTrace();
		 }finally{
		 //in.close();
		 System.out.println("Disconnected !");
		 }
	}

	public static void connect(String user, String pass) {
		ServerSettings sv = Server.SRV_IN_1.getSettings();
		sv.setServerIP("61.120.153.86");
		AppData.initialize();
		AppData.session.setReconnectMaxTryCount(99);
		AppData.session.setReconnectWaitTime(5);
		AppData.session.setReconnectWaitTimeUnit(TimeUnit.SECONDS);
		AppData.session.setReconnetCurrentTimeInSec(10);
		AppData.session.setReconnectActive(true);
		try {
			AppData.session.connect(new ClientSettings(), sv);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		try {
			AppData.session.authenticate();
		} catch (InterruptedException | WaitMsgTimeoutException | DisconnectException | LoginException e) {
			e.printStackTrace();
		}
		System.out.println("Connected: " + AppData.session.isConnected());
		try {
			AppData.session.login(user, pass, null);
		} catch (LoginException e1) {
			System.out.println(AppData.session.getDisconnectReason());
		} catch (DisconnectException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (WaitMsgTimeoutException e1) {
			e1.printStackTrace();
		}
		System.out.println("logged in");
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static void reconnect() {
		if (AppData.session.getDisconnectReason().isConnectionBroken() && AppData.session.isReconnectActive()) {
			try {
				Thread.sleep(AppData.session.getReconnetCurrentTimeInSec() * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				AppData.session.connect(AppData.session.getClientSettings(), AppData.session.getServerSettings());
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				AppData.session.authenticate();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (WaitMsgTimeoutException e1) {
				e1.printStackTrace();
			} catch (DisconnectException e1) {
				e1.printStackTrace();
			} catch (LoginException e1) {
				e1.printStackTrace();
			}
			try {
				AppData.session.login(AppData.session.getLastLoginName(), AppData.session.getLastLoginPassword(),
						AppData.session.getLastAccountId());
			} catch (LoginException e) {
				e.printStackTrace();
			} catch (DisconnectException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (WaitMsgTimeoutException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			selectCastle(selectedCastle);
		}
	}

	public static void printArmy(String castle) {
		selectCastle(castle);
		ObjectIItem[] armySize;
		try {
			for (int length = (armySize = AppData.session.getArmySizeInfo()
					.getArmySize()).length, i = 0; i < length; ++i) {
				final ObjectIItem unitSizeInfo = armySize[i];
				final SoldierUnit soldierUnit = SoldierUnit.getByRawType(unitSizeInfo.getWID());
				if (soldierUnit != null) {
					if (soldierUnit.getWid().getWeaponType() == SoldierWeaponType.ST_RANGED) {
						System.out.print(unitSizeInfo.getWID()+ "\t"
								+ soldierUnit.getWid().getDescription() + " ");
						if (soldierUnit.getWid().getFoodConsumeSize() > 0) {
							System.out.print(
									String.valueOf(unitSizeInfo.getSize() * soldierUnit.getWid().getFoodConsumeSize()));
						}
						System.out.println(" " + soldierUnit.getWid().getWeaponType().getDescription());
					}
				} else {
					final SiegieToolUnit siegieToolUnit = SiegieToolUnit.getByRawType(unitSizeInfo.getWID());
					if (siegieToolUnit != null) {
						System.out.print(String.valueOf(unitSizeInfo.getSize()) + "\t"
								+ siegieToolUnit.getWid().getDescription() + " ");
					} else {
						final DefenceToolUnit defenceToolUnit = DefenceToolUnit.getByRawType(unitSizeInfo.getWID());
						if (defenceToolUnit != null) {
							System.out.print(String.valueOf(
									unitSizeInfo.getSize() + "\t" + defenceToolUnit.getWid().getDescription()));
						} else {
							System.out.print(String.valueOf(unitSizeInfo.getSize()) + "\t" + MessageFormat
									.format(Messages.getString("MainWnd.UNKNOWN_UNIT_TYPE"), unitSizeInfo.getWID()));
						}
					}
					System.out.println();
				}
			}
			ObjectIItem[] armyHospitalInjuredSize;
			for (int length2 = (armyHospitalInjuredSize = AppData.session.getArmySizeInfo()
					.getArmyHospitalInjuredSize()).length, j = 0; j < length2; ++j) {
				final ObjectIItem unitSizeInfo = armyHospitalInjuredSize[j];
				final SoldierUnit soldierUnit = SoldierUnit.getByRawType(unitSizeInfo.getWID());
				if (soldierUnit != null) {
					System.out.print(String.valueOf(unitSizeInfo.getSize()+"\t"+soldierUnit.getWid().getDescription()
							+"\t"+Utils.secondsToHours(unitSizeInfo.getSize() * soldierUnit.getWid().getHealingTime())));
					final ResourcesCurrency cost = soldierUnit.getWid().getHealingCost();
					if (cost.getGoldAmount() > 0.0) {
						System.out.print(String.format("%s * %s = %s", (int) cost.getGoldAmount(),
								unitSizeInfo.getSize(), (int) cost.getGoldAmount() * unitSizeInfo.getSize()));
					} else if (cost.getRubyAmount() > 0.0) {
						System.out.print(String.format("%s * %s = %s", (int) cost.getRubyAmount(),
								unitSizeInfo.getSize(), (int) cost.getRubyAmount() * unitSizeInfo.getSize()));
					}
					System.out.println();
				}
			}
		} catch (pl.autoempire.core.json.JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static CastlePosition castle(String castleName)
	{
		selectedCastle = castleName;
		try {
			for (CastlePosition castlePosition : AppData.session.getAllCastles()) {
				//System.out.println(castlePosition.getCastleName());
				if(castlePosition.getCastleName().equalsIgnoreCase(castleName))
					return castlePosition;
			}
		} catch (pl.autoempire.core.json.JSONException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void selectCastle(String castle)
	{
		try {
			AppData.session.getSyncActionPerformer().goToCastle(castle(castle), false);
		} catch (Throwable e2) {
			e2.printStackTrace();
		}
		try {
			System.out.println("Selected: "+AppData.session.getCastleNameById(AppData.session.getActiveCastleId()));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public static void pause(int min)
	{
		try {
			Thread.sleep(min * 60 * 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	public static void commander()
	{
		ObjectUM_L[] attackLiders;
		try {
			attackLiders = AppData.session.getGameBasicData().getLidersInfo().getAttackLiders();
			for (int length = attackLiders.length, i = 0; i < length; ++i) {
                final ObjectUM_L liderInfo = attackLiders[i];
                System.out.println(liderInfo.getLiderID());
            }
		} catch (pl.autoempire.core.json.JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void fireAttack()
	{
		selectCastle("Reaper Death");
		MsgOutCRA msgCRA = new MsgOutCRA();
		JSONArray A;
		try {
			A = msgCRA.getContent().getJSONArray("A");
			setTroops(A);
		} catch (pl.autoempire.core.json.JSONException e) {
			e.printStackTrace();
		}
		System.out.println(msgCRA.format(AppData.session.getServerSettings().getZoneName(), 1));
		
	}
	private static void setTroops(JSONArray A) throws pl.autoempire.core.json.JSONException {
		ObjectIItem[] armySize = AppData.session.getArmySizeInfo().getArmySize();
		for (ObjectIItem unitSizeInfo : armySize) {
			if(rangeToKill.contains(unitSizeInfo.getWID()));
				
		}
	}

}
