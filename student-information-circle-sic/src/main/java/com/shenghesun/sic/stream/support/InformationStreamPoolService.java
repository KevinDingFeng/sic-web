package com.shenghesun.sic.stream.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.information.entity.Information;
import com.shenghesun.sic.information.entity.InformationType;
import com.shenghesun.sic.information.service.InformationService;
import com.shenghesun.sic.information.service.InformationTypeService;
import com.shenghesun.sic.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 信息流 池 模块
 * 	前提：
 * 		该池只对 已经审核通过的信息数据进行处理
 * 	 
 * 	撤回信息操作：
 * 		当信息数据审核模块发现信息数据存在疑问，需要回撤，不参与信息流推送
 * 	接到回撤命令后，异步通知组装模块，并返回“已发出请求”
 * 	异步接到组装模块的回撤结果后，异步通知审核模块
 * @author kevin
 *
 */
@Service
@Slf4j
public class InformationStreamPoolService {
	
	@Autowired
	private InformationService informationService;
	@Autowired
	private InformationTypeService informationTypeService;
	
	@Autowired
	private InformationStreamAssembleService informationStreamAssembleService;

	/**
	 * 池模块 接收到 “有新的信息数据产生”后，异步开启处理新信息的流程，并同步返回“我收到了”
	 * @return
	 */
	public JSONObject newInformationNotify(long id) {
		//开启 异步的 处理新信息的流程
		this.headleNewInformation(id);
		return JsonUtil.getSuccessJSONObject();
	}
	/**
	 * 处理新信息的流程
	 * 	池模块“处理新数据”，同步通知 组装模块 “有新数据，你尝试组装吧”，并等待结果
	 * 		接收到返回结果“我开始尝试了”后，继续
	 * @return
	 */
	public void headleNewInformation(long id) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				JSONObject result = informationStreamAssembleService.tryAssemble(id);
				// 根据实际需求，添加对返回值处理逻辑
				// 修改使用过的 信息 uuid 的状态
				if(JsonUtil.CODE_SUCCESS_VALUE.equals(result.getString(JsonUtil.CODE_KEY))) {
					log.info("notify assemble success");
					JSONArray resultArr = result.getJSONArray(JsonUtil.DATA_KEY);
					for(int i = 0 ; i < resultArr.size() ; i ++) {
//						String[] uuids = (String[] )result.get(JsonUtil.DATA_KEY);
						String[] uuids = (String[] )resultArr.get(i);
						int n = informationService.updateUsedByUuidIn(uuids);
						if(n < uuids.length) {
							log.info("update information used value error.");
						}	
					}
				}else {
					log.info("notify assemble fail : " + result.getString(JsonUtil.DATA_KEY));
				}
			}
		}.start();
	}
	/**
	 * 判断 typeId 对应类型下的空闲 信息数量
	 * @param typeId
	 * @return
	 */
	public Long getNotUsedNum(long typeId) {
		List<Long> inforIds = this.getInforIdsByTypeId(typeId);
//		Long num = informationService.countByVerifiedAndUsedAndRemoved(true, false, false);
		Long num = informationService.countByVerifiedAndUsedAndRemovedAndIdIn(true, false, false, inforIds);
		return num;
	}
	private List<Long> getInforIdsByTypeId(long typeId){
		InformationType type = informationTypeService.findById(typeId);
		List<Long> inforIds = this.formatIds(type.getInformations());
		if(inforIds == null) {
			return null;
		}
		return inforIds;
	}
	private List<Long> formatIds(Set<Information> set){
		if(set != null && set.size() > 0) {
			List<Long> ids = new ArrayList<>();
			Iterator<Information> it = set.iterator();
			while(it.hasNext()) {
				Information infor = it.next();
				ids.add(infor.getId());
			}
			return ids;
		}
		return null;
	}
	// 该数值为固定配置值，目前配置到 support 层，当拆分项目发生时，该值都需要单独配置到对应的项目中
	private static final int INFORMATION_LIST_PAGE_NUM = 0;// 页数，从 0 开始
	private static final int INFORMATION_LIST_PAGE_SIZE = 5;// 每页的条数
	
	/**
	 * 获取没有 被使用的 信息数据
	 * 	根据业务需求，分页获取
	 * @return
	 */
	public List<Information> getNotUsedInformations(long typeId) {
		List<Long> inforIds = this.getInforIdsByTypeId(typeId);
		if(inforIds == null) {
			return null;
		}
		Pageable pageable = this.getListPageable(INFORMATION_LIST_PAGE_NUM);
		Page<Information> pages = informationService.findBySpecification(this.getNotUsedSpecification(inforIds), pageable);
		if(pages != null && pages.getContent() != null && pages.getContent().size() > 0) {
			return pages.getContent();
		}
		return null;
	}
	
	private Pageable getListPageable(Integer pageNum) {
		Sort sort = new Sort(Direction.DESC, "creation");
		Pageable pageable = new PageRequest(pageNum, INFORMATION_LIST_PAGE_SIZE, sort);
		return pageable;
	}
	/**
	 * 根据特殊的需求 整理组合查询条件
	 * @return
	 */
	private Specification<Information> getNotUsedSpecification(List<Long> inforIds) {
		return new Specification<Information>() {
			@Override
			public Predicate toPredicate(Root<Information> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				list.add(cb.equal(root.get("removed"), false));
				list.add(cb.equal(root.get("used"), false));
				list.add(cb.equal(root.get("verified"), true));
				if(inforIds != null) {//尝试，不一定好用
					In<Long> ins = cb.in(root.get("id"));
					for(Long id : inforIds) {
						ins.value(id);
					}
					list.add(cb.and(ins));
				}
				
				Predicate[] p = new Predicate[list.size()];
				return cb.and(list.toArray(p));
			}
		};
	}
	public String getDefaultType() {
		return informationTypeService.getDefaultType();
	}
	/**
	 * 根据 id 获取信息的类型，不存在则返回 null
	 * @param id
	 * @return
	 */
	public Set<InformationType> findTypesById(long id) {
		Information infor = informationService.findOne(id);
		if(infor != null) {
			Set<InformationType> types = infor.getTypes();
			if(types != null && types.size() > 0) {
				return types;
			}
		}
		return null;
	}
	
}
