package org.phototimemachine.service;

import org.phototimemachine.domain.Topic;

import java.util.List;

public interface TopicService {

    public Topic findById(Long id);
    public List<Topic> findAllByGroup(Long group_id);
    public List<Topic> findAllByGroup(Long group_id, int limit);
    public int countByGroup(Long group_id);
    public Topic save(Topic topic);
    public void delete(Topic topic);
}
