class Note < ActiveRecord::Base
  belongs_to :user
  belongs_to :photo

  validates :message, :user_id, :photo_id, :presence => true

  def author
    user = User.where('id = ?', user_id).last
    if user
      user.full_name
    end
  end

  def is_admin?(user)
    user_id == user.id
  end
end
