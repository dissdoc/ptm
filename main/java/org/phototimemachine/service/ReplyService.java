package org.phototimemachine.service;

import org.phototimemachine.domain.Reply;

public interface ReplyService {

    public Reply findById(Long id);
    public Reply save(Reply reply);
    public void delete(Reply reply);
}
