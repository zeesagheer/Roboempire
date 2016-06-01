
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import pl.autoempire.core.json.JSONArray;
import pl.autoempire.core.json.JSONException;
import pl.autoempire.core.CastlePosition;
import pl.autoempire.core.ResourcesCurrency;
import pl.autoempire.core.Utils;
import pl.autoempire.core.connection.ClientSettings;
import pl.autoempire.core.connection.DisconnectException;
import pl.autoempire.core.connection.LoginException;
import pl.autoempire.core.connection.WaitMsgTimeoutException;
import pl.autoempire.core.dict.DefenceToolUnit;
import pl.autoempire.core.dict.EquipmentType;
import pl.autoempire.core.dict.SiegieToolUnit;
import pl.autoempire.core.dict.SoldierUnit;
import pl.autoempire.core.dict.SoldierWeaponType;
import pl.autoempire.core.messages.JSON.MsgOutCRA;
import pl.autoempire.core.objects.ObjectAIItem;
import pl.autoempire.core.objects.ObjectEQItem;
import pl.autoempire.core.objects.ObjectEQItem_Effect;
import pl.autoempire.core.objects.ObjectIItem;
import pl.autoempire.core.objects.ObjectUM_L;
import pl.autoempire.core.servers.Server;
import pl.autoempire.core.servers.ServerSettings;
import pl.autoempire.gui.AppData;
import pl.autoempire.gui.resources.messages.Messages;

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
			try {
				getSlotInfo();
				System.out.println(EquipmentType.getByRawType(AppData.session.getGameBasicData().getLidersInfo().getAttackLiders()[14].getEquipment()[4].getType()));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
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
		} catch (JSONException e) {
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
		} catch (JSONException e) {
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
	public static void commander(int num)
	{
		
		ObjectUM_L[] attackLiders;
		try {
			attackLiders = AppData.session.getGameBasicData().getLidersInfo().getAttackLiders();
			for (int length = attackLiders.length, i = 0; i < length; ++i) {
                final ObjectUM_L liderInfo = attackLiders[i];
                System.out.println(liderInfo.getLiderName());
                for (ObjectEQItem equips : liderInfo.getEquipment()) {
                	System.out.println(equips.getGemID());
//					
                	//for (ObjectEQItem_Effect eq_effect : equips.getEffects()) {
//						System.out.println(eq_effect.getEffectId());
//					}
				}
            }
		} catch (JSONException e) {
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(msgCRA.format(AppData.session.getServerSettings().getZoneName(), 1));
		
	}
	private static void setTroops(JSONArray A) throws JSONException {
		int value = 0;
		ObjectIItem[] armySize = AppData.session.getArmySizeInfo().getArmySize();
		for (int i = 0; i < 4; i++) {
			for (ObjectIItem unitSizeInfo : armySize) {
				if(rangeToKill.contains(unitSizeInfo.getWID()));
				{
					JSONArray troopSlot = A.getJSONObject(i).getJSONObject("L").getJSONArray("U").getJSONArray(0);
					troopSlot.put(0, unitSizeInfo.getWID());
					troopSlot.put(1, value);
				}
			}
		}
	}
	public static AttackSlotSize getSlotInfo() throws JSONException
	{
		int flankGems[][] = {{230,1},{231,4},{232,7},{233,10},{234,12},{235,15},
				{236,17},{237,20},{238,22},{239,25},{305,26},{306,27},{307,29}};
		int frontGems[][] = {{220,1},{221,3},{222,5},{223,7},{224,8},{225,10},
				{226,11},{227,14},{228,15},{229,17},{302,18},{303,19},{304,20}};
		int flankBonus=0;
		int frontBonus=0;
		int baseFlank = 64;
		int baseFront = 192;
		int commanderIndex = 14;
		AttackSlotSize attackSlotSize = new AttackSlotSize();
		for (int  i : AppData.session.getGameBasicData().getLidersInfo().getAttackLiders()[2].getGems()) {
			for (int[] j : flankGems) {
				if(i==j[0])
					flankBonus+=j[1];
			}
			for (int[] j : frontGems) {
				if(i==j[0])
					frontBonus+=j[1];
			}
		}
		for (ObjectEQItem equip : AppData.session.getGameBasicData().getLidersInfo().getAttackLiders()[commanderIndex].getEquipment()) {
			if(equip.getType() == EquipmentType.HERO.getRawType()) {
				for(ObjectEQItem_Effect eqEffect : equip.getEffects()) {
						System.out.println(eqEffect.getEffectId());
						System.out.println(eqEffect.getStrengthID());
				}
			}
		}
		System.out.println(frontBonus);
		attackSlotSize.setFlank((int) Math.round(baseFlank*(1+flankBonus*0.01)));
		attackSlotSize.setFront((int) Math.round(baseFront*(1+frontBonus*0.01)));
		return attackSlotSize;
	}
	
	public static void printCastle()
	{
		try {
			AppData.session.refreshMapCastles(AppData.session.getCastleInfo().getCastleShortInfo().getKingdomId());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ArrayList<ObjectAIItem> castles;
		try {
			castles = AppData.session.getMapCastles(AppData.session.getCastleInfo().getCastleShortInfo().getKingdomId());
		
        if (castles == null) {
            return ;
        }
        for (final ObjectAIItem mapCastle : castles) {
            try {
				System.out.println(mapCastle.getCastleName());
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

}
