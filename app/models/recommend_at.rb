class RecommendAt < ActiveRecord::Base
  belongs_to :user
  belongs_to :photo

  attr_accessible :from_at, :to_at
  validates :user_id, :photo_id, :presence => true
end
