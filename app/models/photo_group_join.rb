class PhotoGroupJoin < ActiveRecord::Base
  belongs_to :photo
  belongs_to :group

  validates :photo_id, :group_id, :presence => true
end
