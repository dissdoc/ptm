class UserMailer < ActionMailer::Base
  default :from => 'hello@phototimemachine.org'

  def added_photo_favorite(user, photo)
    @user = user
    @photo = photo

    mail(
        :to => photo.user.email,
        :subject => "#{user.full_name} added your photo '#{photo.comment}' as a favorite"
    )
  end

  def added_tag(user, tags, photo)
    @user = user
    @tags = tags
    @photo = photo

    mail(
        :to => photo.user.email,
        :subject => "#{user.full_name} added tag to your photo '#{photo.comment}'"
    )
  end

  def suggested_geotag(user, photo)
    @user = user
    @photo = photo

    mail(
        :to => photo.user.email,
        :subject => "#{user.full_name} suggested geo tag to your photo '#{photo.comment}'"
    )
  end

  def shared_photo(user, photo)
    @user = user
    @photo = photo

    mail(
        :to => photo.user.email,
        :subject => "#{user.full_name} shared photo '#{photo.comment}'"
    )
  end

  def added_note(user, note, photo)
    @user = user
    @note = note
    @photo = photo

    mail(
        :to => photo.user.email,
        :subject => "#{user.full_name} added note to photo '#{photo.comment}'"
    )
  end

  def also_added_comment(user, photo, current_notes)
    @user = user
    @photo = photo

    emails = current_notes.collect(&:user).collect(&:email).join(";")
    mail(
        :to => emails,
        :subject => "#{user.full_name} also added a comment to photo '#{photo.comment}'"
    )
  end

  def commented_photo(user, photo)
    @user = user
    @photo = photo

    mail(
        :to => photo.user.email,
        :subject => "#{user.full_name} commented photo '#{photo.comment}'"
    )
  end

  def added_photo_gallery(user, gallery, photo)
    @user = user
    @gallery = gallery
    @photo = photo

    mail(
        :to => photo.user.email,
        :subject => "#{user.full_name} added photo '#{photo.comment}' to the gallery"
    )
  end

  def added_friend(user, you)
    @user = user

    mail(
        :to => you.email,
        :subject => "#{user.full_name} added you as a friend"
    )
  end

  def invited_photo(user, group, photo)
    @user = user
    @photo = photo
    @group = group

    mail(
        :to => photo.user.email,
        :subject => "#{user.full_name} invited your photo to group"
    )
  end

  def invited_you(user, you, group)
    @user = user
    @group = group

    mail(
        :to => you.email,
        :subject => "#{user.full_name} invited you to group"
    )
  end
end
