class UserMailer < ActionMailer::Base

  def added_photo_favorite(user, photo)
    recipients "#{photo.user.full_name} <#{photo.user.email}>"
    from "Photo Time Machine"
    subject "#{user.full_name} added your photo '#{photo.comment}' as a favorite"
    sent_on Time.now
  end
end
