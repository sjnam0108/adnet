package net.doohad.models.service;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.models.org.OrgAlimTalk;
import net.doohad.models.org.OrgChanSub;
import net.doohad.models.org.OrgChannel;
import net.doohad.models.org.OrgMediumOpt;
import net.doohad.models.org.OrgRTChannel;
import net.doohad.models.org.OrgRadRegion;
import net.doohad.models.org.OrgSiteCond;

@Transactional
public interface OrgService {
	
	// Common
	public void flush();

	
	//
	// for OrgAdvertiser
	//
	// Common
	public OrgAdvertiser getAdvertiser(int id);
	public void saveOrUpdate(OrgAdvertiser advertiser);
	public void deleteAdvertiser(OrgAdvertiser advertiser);
	public void deleteAdvertisers(List<OrgAdvertiser> advertisers);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdvertiserList(DataSourceRequest request);

	// for DAO specific
	public OrgAdvertiser getAdvertiser(KnlMedium medium, String name);
	public List<OrgAdvertiser> getAdvertiserListByMediumId(int mediumId);

	
	//
	// for OrgSiteCond
	//
	// Common
	public OrgSiteCond getSiteCond(int id);
	public void saveOrUpdate(OrgSiteCond siteCond);
	public void deleteSiteCond(OrgSiteCond siteCond);
	public void deleteSiteConds(List<OrgSiteCond> siteConds);

	// for Kendo Grid Remote Read
	public DataSourceResult getSiteCondList(DataSourceRequest request);

	// for DAO specific
	public OrgSiteCond getSiteCond(KnlMedium medium, String code);
	public List<OrgSiteCond> getSiteCondListByMediumId(int mediumId);
	public List<OrgSiteCond> getSiteCondListByMediumIdActiveStatus(
			int mediumId, boolean activeStatus);
	public List<OrgSiteCond> getSiteCondListByMediumIdNameLike(int mediumId, String name);

	
	//
	// for OrgMediumOpt
	//
	// Common
	public OrgMediumOpt getMediumOpt(int id);
	public void saveOrUpdate(OrgMediumOpt mediumOpt);
	public void deleteMediumOpt(OrgMediumOpt mediumOpt);
	public void deleteMediumOpts(List<OrgMediumOpt> mediumOpts);

	// for Kendo Grid Remote Read

	// for DAO specific
	public OrgMediumOpt getMediumOpt(KnlMedium medium, String code);
	public List<OrgMediumOpt> getMediumOptListByMediumId(int mediumId);
	public String getMediumOptValue(int mediumId, String code);

	
	//
	// for OrgChannel
	//
	// Common
	public OrgChannel getChannel(int id);
	public void saveOrUpdate(OrgChannel channel);
	public void deleteChannel(OrgChannel channel);
	public void deleteChannels(List<OrgChannel> channels);

	// for Kendo Grid Remote Read
	public DataSourceResult getChannelList(DataSourceRequest request);

	// for DAO specific
	public OrgChannel getChannel(KnlMedium medium, String shortName);
	public List<OrgChannel> getChannelListByMediumId(int mediumId);
	public List<OrgChannel> getChannelListByMediumIdActiveStatus(int mediumId, boolean activeStatus);
	public List<OrgChannel> getActiveChannelList();
	public List<Tuple> getChannelTupleListByTypeObjId(String type, int objId);
	public List<OrgChannel> getAdAppendableChannelList();

	
	//
	// for OrgChanSub
	//
	// Common
	public OrgChanSub getChanSub(int id);
	public void saveOrUpdate(OrgChanSub chanSub);
	public void deleteChanSub(OrgChanSub chanSub);
	public void deleteChanSubs(List<OrgChanSub> chanSubs);

	// for Kendo Grid Remote Read
	public DataSourceResult getChanSubList(DataSourceRequest request, String type, int channelId);

	// for DAO specific
	public OrgChanSub getChanSub(OrgChannel channel, String type, int objId);
	public int getChanSubCountByChannelId(int channelId);
	public List<Tuple> getChanSubScrTupleListByChannelId(int channelId);
	public List<Tuple> getChanSubSyncPackTupleListByChannelId(int channelId);
	public void deleteChanSubsBySyncPackId(int syncPackId);

	
	//
	// for OrgRadRegion
	//
	// Common
	public OrgRadRegion getRadRegion(int id);
	public void saveOrUpdate(OrgRadRegion region);
	public void deleteRadRegion(OrgRadRegion region);
	public void deleteRadRegions(List<OrgRadRegion> regions);

	// for Kendo Grid Remote Read
	public DataSourceResult getRadRegionList(DataSourceRequest request);
	public DataSourceResult getActiveRadRegionList(DataSourceRequest request);

	// for DAO specific
	public List<OrgRadRegion> getRadRegionListByMediumId(int mediumId);
	public List<OrgRadRegion> getRadRegionListByMediumIdActiveStatus(int mediumId, boolean activeStatus);

	
	//
	// for OrgRTChannel
	//
	// Common
	public OrgRTChannel getRTChannel(int id);
	public void saveOrUpdate(OrgRTChannel rtChannel);
	public void deleteRTChannel(OrgRTChannel rtChannel);
	public void deleteRTChannels(List<OrgRTChannel> rtChannels);

	// for Kendo Grid Remote Read

	// for DAO specific
	public OrgRTChannel getRTChannelByChannelId(int channelId);

	
	//
	// for OrgAlimTalk
	//
	// Common
	public OrgAlimTalk getAlimTalk(int id);
	public void saveOrUpdate(OrgAlimTalk alimTalk);
	public void deleteAlimTalk(OrgAlimTalk alimTalk);
	public void deleteAlimTalks(List<OrgAlimTalk> alimTalks);

	// for Kendo Grid Remote Read
	public DataSourceResult getAlimTalkList(DataSourceRequest request);

	// for DAO specific
	public OrgAlimTalk getAlimTalk(KnlMedium medium, String shortName);
	public List<OrgAlimTalk> getActiveAlimTalkList();

	
	//
	// for Common
	//
	public void deleteSoftAdvertiser(OrgAdvertiser advertiser);

}
