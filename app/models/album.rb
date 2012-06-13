class Album < ActiveRecord::Base
  belongs_to :user

  has_many :photos, :dependent => :destroy
  accepts_nested_attributes_for :photos, :allow_destroy => true,
      :reject_if => proc{ |attrs| attrs[:image].present? || attrs[:id].present?}

  validates :name, :user_id, :presence => true

  def show_thumbnail
    photos.where("id = ?", thumbnail).first
  end
end
