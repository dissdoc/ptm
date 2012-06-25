class PhotoGroupJoin < ActiveRecord::Base
  belongs_to :photo
  belongs_to :group

  validates :photo_id, :group_id, :user_id, :presence => true

  def accept!
    update_attribute(:status, true)
  end
end
