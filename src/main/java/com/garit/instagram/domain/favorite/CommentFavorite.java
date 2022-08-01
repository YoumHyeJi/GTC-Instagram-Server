package com.garit.instagram.domain.favorite;

import com.garit.instagram.domain.comment.Comment;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
public class CommentFavorite extends Favorite{

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_comment_id")
    private Comment comment;
}
