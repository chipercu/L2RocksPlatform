package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.instancemanager.CastleManager;
import com.fuzzy.subsystem.gameserver.instancemanager.CastleManorManager;
import com.fuzzy.subsystem.gameserver.instancemanager.CastleManorManager.CropProcure;
import com.fuzzy.subsystem.gameserver.model.L2Manor;
import com.fuzzy.subsystem.gameserver.model.entity.residence.Castle;
import com.fuzzy.subsystem.util.GArray;

/**
 * format
 * dd[ddc[d]c[d]ddddddcddc]
 * dd[ddc[d]c[d]ddddQQcQQc] - Gracia Final
 */
public class ExShowCropSetting extends L2GameServerPacket
{
	private int _manorId;
	private int _count;
	private long[] _cropData; // data to send, size:_count*14

	public ExShowCropSetting(int manorId)
	{
		_manorId = manorId;
		Castle c = CastleManager.getInstance().getCastleByIndex(_manorId);
		GArray<Integer> crops = L2Manor.getInstance().getCropsForCastle(_manorId);
		_count = crops.size();
		_cropData = new long[_count * 14];
		int i = 0;
		for(int cr : crops)
		{
			_cropData[i * 14 + 0] = cr;
			_cropData[i * 14 + 1] = L2Manor.getInstance().getSeedLevelByCrop(cr);
			_cropData[i * 14 + 2] = L2Manor.getInstance().getRewardItem(cr, 1);
			_cropData[i * 14 + 3] = L2Manor.getInstance().getRewardItem(cr, 2);
			_cropData[i * 14 + 4] = L2Manor.getInstance().getCropPuchaseLimit(cr);
			_cropData[i * 14 + 5] = 0; // Looks like not used
			_cropData[i * 14 + 6] = L2Manor.getInstance().getCropBasicPrice(cr) * 60 / 100;
			_cropData[i * 14 + 7] = L2Manor.getInstance().getCropBasicPrice(cr) * 10;
			CropProcure cropPr = c.getCrop(cr, CastleManorManager.PERIOD_CURRENT);
			if(cropPr != null)
			{
				_cropData[i * 14 + 8] = cropPr.getStartAmount();
				_cropData[i * 14 + 9] = cropPr.getPrice();
				_cropData[i * 14 + 10] = cropPr.getReward();
			}
			else
			{
				_cropData[i * 14 + 8] = 0;
				_cropData[i * 14 + 9] = 0;
				_cropData[i * 14 + 10] = 0;
			}
			cropPr = c.getCrop(cr, CastleManorManager.PERIOD_NEXT);
			if(cropPr != null)
			{
				_cropData[i * 14 + 11] = cropPr.getStartAmount();
				_cropData[i * 14 + 12] = cropPr.getPrice();
				_cropData[i * 14 + 13] = cropPr.getReward();
			}
			else
			{
				_cropData[i * 14 + 11] = 0;
				_cropData[i * 14 + 12] = 0;
				_cropData[i * 14 + 13] = 0;
			}
			i++;
		}
	}

	@Override
	public void writeImpl()
	{
		writeC(EXTENDED_PACKET); // Id
		writeH(0x2b); // SubId

		writeD(_manorId); // manor id
		writeD(_count); // size

		for(int i = 0; i < _count; i++)
		{
			writeD((int) _cropData[i * 14 + 0]); // crop id
			writeD((int) _cropData[i * 14 + 1]); // seed level

			writeC(1);
			writeD((int) _cropData[i * 14 + 2]); // reward 1 id

			writeC(1);
			writeD((int) _cropData[i * 14 + 3]); // reward 2 id

			writeD((int) _cropData[i * 14 + 4]); // next sale limit
			writeD((int) _cropData[i * 14 + 5]); // ???
			writeD((int) _cropData[i * 14 + 6]); // min crop price
			writeD((int) _cropData[i * 14 + 7]); // max crop price

			writeQ(_cropData[i * 14 + 8]); // today buy
			writeQ(_cropData[i * 14 + 9]); // today price
			writeC((int) _cropData[i * 14 + 10]); // today reward
			writeQ(_cropData[i * 14 + 11]); // next buy
			writeQ(_cropData[i * 14 + 12]); // next price

			writeC((int) _cropData[i * 14 + 13]); // next reward
		}
	}
}