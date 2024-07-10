package kr.adnetwork.models.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgAdvertiser;
import kr.adnetwork.models.org.OrgAlimTalk;
import kr.adnetwork.models.org.OrgChanSub;
import kr.adnetwork.models.org.OrgChannel;
import kr.adnetwork.models.org.OrgMediumOpt;
import kr.adnetwork.models.org.OrgRTChannel;
import kr.adnetwork.models.org.OrgRadRegion;
import kr.adnetwork.models.org.OrgSiteCond;
import kr.adnetwork.models.org.dao.OrgAdvertiserDao;
import kr.adnetwork.models.org.dao.OrgAlimTalkDao;
import kr.adnetwork.models.org.dao.OrgChanSubDao;
import kr.adnetwork.models.org.dao.OrgChannelDao;
import kr.adnetwork.models.org.dao.OrgMediumOptDao;
import kr.adnetwork.models.org.dao.OrgRTChannelDao;
import kr.adnetwork.models.org.dao.OrgRadRegionDao;
import kr.adnetwork.models.org.dao.OrgSiteCondDao;
import kr.adnetwork.utils.Util;

@Transactional
@Service("orgService")
public class OrgServiceImpl implements OrgService {

    //
    // General
    //
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public void flush() {
		
		sessionFactory.getCurrentSession().flush();
	}

	
    
    //
    // DAO
    //
    @Autowired
    private OrgAdvertiserDao advertiserDao;

    @Autowired
    private OrgSiteCondDao siteCondDao;

    @Autowired
    private OrgChannelDao channelDao;

    @Autowired
    private OrgChanSubDao chanSubDao;

    @Autowired
    private OrgMediumOptDao mediumOptDao;

    @Autowired
    private OrgRadRegionDao radRegionDao;
    
    @Autowired
    private OrgRTChannelDao rtChannelDao;
    
    @Autowired
    private OrgAlimTalkDao alimTalkDao;

    
    
	//
	// for OrgAdvertiserDao
	//
	@Override
	public OrgAdvertiser getAdvertiser(int id) {
		return advertiserDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgAdvertiser advertiser) {
		advertiserDao.saveOrUpdate(advertiser);
	}

	@Override
	public void deleteAdvertiser(OrgAdvertiser advertiser) {
		advertiserDao.delete(advertiser);
	}

	@Override
	public void deleteAdvertisers(List<OrgAdvertiser> advertisers) {
		advertiserDao.delete(advertisers);
	}

	@Override
	public DataSourceResult getAdvertiserList(DataSourceRequest request) {
		return advertiserDao.getList(request);
	}

	@Override
	public OrgAdvertiser getAdvertiser(KnlMedium medium, String name) {
		return advertiserDao.get(medium, name);
	}

	@Override
	public List<OrgAdvertiser> getAdvertiserListByMediumId(int mediumId) {
		return advertiserDao.getListByMediumId(mediumId);
	}

    
    
	//
	// for OrgSiteCondDao
	//
	@Override
	public OrgSiteCond getSiteCond(int id) {
		return siteCondDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgSiteCond siteCond) {
		siteCondDao.saveOrUpdate(siteCond);
	}

	@Override
	public void deleteSiteCond(OrgSiteCond siteCond) {
		siteCondDao.delete(siteCond);
	}

	@Override
	public void deleteSiteConds(List<OrgSiteCond> siteConds) {
		siteCondDao.delete(siteConds);
	}

	@Override
	public DataSourceResult getSiteCondList(DataSourceRequest request) {
		return siteCondDao.getList(request);
	}

	@Override
	public OrgSiteCond getSiteCond(KnlMedium medium, String code) {
		return siteCondDao.get(medium, code);
	}

	@Override
	public List<OrgSiteCond> getSiteCondListByMediumId(int mediumId) {
		return siteCondDao.getListByMediumId(mediumId);
	}

	@Override
	public List<OrgSiteCond> getSiteCondListByMediumIdActiveStatus(int mediumId, boolean activeStatus) {
		return siteCondDao.getListByMediumIdActiveStatus(mediumId, activeStatus);
	}

	@Override
	public List<OrgSiteCond> getSiteCondListByMediumIdNameLike(int mediumId, String name) {
		return siteCondDao.getListByMediumIdNameLike(mediumId, name);
	}

    
    
	//
	// for OrgMediumOptDao
	//
	@Override
	public OrgMediumOpt getMediumOpt(int id) {
		return mediumOptDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgMediumOpt mediumOpt) {
		mediumOptDao.saveOrUpdate(mediumOpt);
	}

	@Override
	public void deleteMediumOpt(OrgMediumOpt mediumOpt) {
		mediumOptDao.delete(mediumOpt);
	}

	@Override
	public void deleteMediumOpts(List<OrgMediumOpt> mediumOpts) {
		mediumOptDao.delete(mediumOpts);
	}

	@Override
	public OrgMediumOpt getMediumOpt(KnlMedium medium, String code) {
		return mediumOptDao.get(medium, code);
	}

	@Override
	public List<OrgMediumOpt> getMediumOptListByMediumId(int mediumId) {
		return mediumOptDao.getListByMediumId(mediumId);
	}

	@Override
	public String getMediumOptValue(int mediumId, String code) {
		return mediumOptDao.getValue(mediumId, code);
	}

    
    
	//
	// for OrgChannelDao
	//
	@Override
	public OrgChannel getChannel(int id) {
		return channelDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgChannel channel) {
		channelDao.saveOrUpdate(channel);
	}

	@Override
	public void deleteChannel(OrgChannel channel) {
		channelDao.delete(channel);
	}

	@Override
	public void deleteChannels(List<OrgChannel> channels) {
		channelDao.delete(channels);
	}

	@Override
	public DataSourceResult getChannelList(DataSourceRequest request) {
		return channelDao.getList(request);
	}

	@Override
	public OrgChannel getChannel(KnlMedium medium, String shortName) {
		return channelDao.get(medium, shortName);
	}

	@Override
	public List<OrgChannel> getChannelListByMediumId(int mediumId) {
		return channelDao.getListByMediumId(mediumId);
	}

	@Override
	public List<OrgChannel> getChannelListByMediumIdActiveStatus(int mediumId, boolean activeStatus) {
		return channelDao.getListByMediumIdActiveStatus(mediumId, activeStatus);
	}

	@Override
	public List<OrgChannel> getActiveChannelList() {
		return channelDao.getActiveList();
	}

	@Override
	public List<Tuple> getChannelTupleListByTypeObjId(String type, int objId) {
		return channelDao.getTupleListByTypeObjId(type, objId);
	}

	@Override
	public List<OrgChannel> getAdAppendableChannelList() {
		return channelDao.getAdAppendableList();
	}

    
    
	//
	// for OrgChanSubDao
	//
	@Override
	public OrgChanSub getChanSub(int id) {
		return chanSubDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgChanSub chanSub) {
		chanSubDao.saveOrUpdate(chanSub);
	}

	@Override
	public void deleteChanSub(OrgChanSub chanSub) {
		chanSubDao.delete(chanSub);
	}

	@Override
	public void deleteChanSubs(List<OrgChanSub> chanSubs) {
		chanSubDao.delete(chanSubs);
	}

	@Override
	public DataSourceResult getChanSubList(DataSourceRequest request, String type, int channelId) {
		return chanSubDao.getList(request, type, channelId);
	}

	@Override
	public OrgChanSub getChanSub(OrgChannel channel, String type, int objId) {
		return chanSubDao.get(channel, type, objId);
	}

	@Override
	public int getChanSubCountByChannelId(int channelId) {
		return chanSubDao.getCountByChannelId(channelId);
	}

	@Override
	public List<Tuple> getChanSubScrTupleListByChannelId(int channelId) {
		return chanSubDao.getScrTupleListByChannelId(channelId);
	}

	@Override
	public List<Tuple> getChanSubSyncPackTupleListByChannelId(int channelId) {
		return chanSubDao.getSyncPackTupleListByChannelId(channelId);
	}

	@Override
	public void deleteChanSubsBySyncPackId(int syncPackId) {
		chanSubDao.deleteBySyncPackId(syncPackId);
	}

    
    
	//
	// for OrgRadRegionDao
	//
	@Override
	public OrgRadRegion getRadRegion(int id) {
		return radRegionDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgRadRegion region) {
		radRegionDao.saveOrUpdate(region);
	}

	@Override
	public void deleteRadRegion(OrgRadRegion region) {
		radRegionDao.delete(region);
	}

	@Override
	public void deleteRadRegions(List<OrgRadRegion> regions) {
		radRegionDao.delete(regions);
	}

	@Override
	public DataSourceResult getRadRegionList(DataSourceRequest request) {
		return radRegionDao.getList(request);
	}

	@Override
	public DataSourceResult getActiveRadRegionList(DataSourceRequest request) {
		return radRegionDao.getActiveList(request);
	}

	@Override
	public List<OrgRadRegion> getRadRegionListByMediumId(int mediumId) {
		return radRegionDao.getListByMediumId(mediumId);
	}

	@Override
	public List<OrgRadRegion> getRadRegionListByMediumIdActiveStatus(int mediumId, boolean activeStatus) {
		return radRegionDao.getListByMediumIdActiveStatus(mediumId, activeStatus);
	}

    
    
	//
	// for OrgRTChannelDao
	//
	@Override
	public OrgRTChannel getRTChannel(int id) {
		return rtChannelDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgRTChannel rtChannel) {
		rtChannelDao.saveOrUpdate(rtChannel);
	}

	@Override
	public void deleteRTChannel(OrgRTChannel rtChannel) {
		rtChannelDao.delete(rtChannel);
	}

	@Override
	public void deleteRTChannels(List<OrgRTChannel> rtChannels) {
		rtChannelDao.delete(rtChannels);
	}

	@Override
	public OrgRTChannel getRTChannelByChannelId(int channelId) {
		return rtChannelDao.getByChannelId(channelId);
	}

    
    
	//
	// for OrgAlimTalkDao
	//
	@Override
	public OrgAlimTalk getAlimTalk(int id) {
		return alimTalkDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgAlimTalk alimTalk) {
		alimTalkDao.saveOrUpdate(alimTalk);
	}

	@Override
	public void deleteAlimTalk(OrgAlimTalk alimTalk) {
		alimTalkDao.delete(alimTalk);
	}

	@Override
	public void deleteAlimTalks(List<OrgAlimTalk> alimTalks) {
		alimTalkDao.delete(alimTalks);
	}

	@Override
	public DataSourceResult getAlimTalkList(DataSourceRequest request) {
		return alimTalkDao.getList(request);
	}

	@Override
	public OrgAlimTalk getAlimTalk(KnlMedium medium, String shortName) {
		return alimTalkDao.get(medium, shortName);
	}

	@Override
	public List<OrgAlimTalk> getActiveAlimTalkList() {
		return alimTalkDao.getActiveList();
	}


	
	//
	// for Common
	//
	@Override
	public void deleteSoftAdvertiser(OrgAdvertiser advertiser) {
		
		if (advertiser != null) {
			
			advertiser.setDeleted(true);
			advertiser.setName(advertiser.getName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			advertiser.setDomainName(advertiser.getDomainName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			
			// OrgAdvertiser에는 최근 변경 who 컬럼이 없어 touch 생략
			
			advertiserDao.saveOrUpdate(advertiser);
		}
	}

}
